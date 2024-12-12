package org.example

import dev.vexide.hydrozoa.CompetitionRobot
import dev.vexide.hydrozoa.devices.Controller
import dev.vexide.hydrozoa.Peripherals
import dev.vexide.hydrozoa.DeviceException
import dev.vexide.hydrozoa.devices.smart.EncoderPosition
import dev.vexide.hydrozoa.devices.smart.Motor
import dev.vexide.hydrozoa.devices.smart.Motor.BrakeMode
import dev.vexide.hydrozoa.devices.smart.MotorControl
import dev.vexide.hydrozoa.devices.smart.MotorControl.Brake
import dev.vexide.hydrozoa.devices.smart.MotorControl.Voltage

private const val GEAR_RATIO = 7
private const val ARM_SPEED = Int.MAX_VALUE

private val ARM_DOWN = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val ElBOW_DOWN = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val WRIST_DOWN = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)

private val ARM_FORWARD = EncoderPosition.ofDegrees(30.0 * GEAR_RATIO)
private val ElBOW_FORWARD = EncoderPosition.ofDegrees(120.0 * GEAR_RATIO)
private val WRIST_FORWARD = EncoderPosition.ofDegrees(-100.0 * GEAR_RATIO)

private val ARM_LOW = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val ElBOW_LOW = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val WRIST_LOW = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)

private val ARM_HIGH = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val ElBOW_HIGH = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val WRIST_HIGH = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)


class Robot(peripherals: Peripherals):  CompetitionRobot{
    var right_Drive = Motor(peripherals.takePort(1), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var left_Drive = Motor(peripherals.takePort(2), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var arm = Motor(peripherals.takePort(3), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var elbow = Motor(peripherals.takePort(4), Motor.Gearset.Red, Motor.Direction.REVERSE)
    var claw = Motor(peripherals.takePort(5), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var wrist = Motor(peripherals.takePort(6), Motor.Gearset.Green, Motor.Direction.FORWARD)
    var controller = peripherals.takeController(Controller.Id.Primary)
    override fun driverInit()  {
        elbow.target = Brake(BrakeMode.BRAKE)
        arm.target = Brake(BrakeMode.BRAKE)
        wrist.target = Brake(BrakeMode.BRAKE)
    }


    override fun driverPeriodic() {

        val input = controller.state.orElseGet(Controller.State::empty)

        val forward = input.leftStick.y
        val turn = input.rightStick.x

        val leftVoltage = (turn + forward) * Motor.V5_MAX_VOLTAGE
        val rightVoltage = (turn - forward) * Motor.V5_MAX_VOLTAGE

        try {
            left_Drive.target = Voltage(leftVoltage)
            right_Drive.target = Voltage(rightVoltage)

            if (input.r2.pressed) {
                claw.target = Voltage(10.0)
            } else if (input.l2.pressed) {
                claw.target = Voltage(-10.0)
            } else {
                claw.target = Brake(BrakeMode.HOLD)
            }

            if (input.b.isNowPressed) {
                //  down
                arm.target = MotorControl.Position(ARM_DOWN, ARM_SPEED)
                elbow.target = MotorControl.Position(ElBOW_DOWN, ARM_SPEED)
                wrist.target = MotorControl.Position(WRIST_DOWN, ARM_SPEED)
            }

            if (input.a.isNowPressed) {
                //mid score
                arm.target = MotorControl.Position(ARM_LOW, ARM_SPEED)
                elbow.target = MotorControl.Position(ElBOW_LOW, ARM_SPEED)
                wrist.target = MotorControl.Position(WRIST_LOW, ARM_SPEED)
            }
//
//            if (input.x.isNowPressed) {
//                // high score
//                arm.target = MotorControl.Position(ARM_UP, ARM_SPEED)
//                elbow.target = MotorControl.Position(ElBOW_UP, ARM_SPEED)
//            }
//
            if (input.y.isNowPressed) {
                // forward grab
                arm.target = MotorControl.Position(ARM_FORWARD, ARM_SPEED)
                elbow.target = MotorControl.Position(ElBOW_FORWARD, ARM_SPEED)
                wrist.target = MotorControl.Position(WRIST_FORWARD, ARM_SPEED)
            }

        } catch (err: DeviceException) {
            throw RuntimeException(err)
        }
    }

    override fun autonomousInit() {
        arm.target = MotorControl.Position(ARM_HIGH, ARM_SPEED)
    }

}