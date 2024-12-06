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
private val ARM_UP = EncoderPosition.ofDegrees(110.0 * GEAR_RATIO)
private val ARM_DOWN = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val ARM_MID = EncoderPosition.ofDegrees(52.0 * GEAR_RATIO)
private val ElBOW_UP = EncoderPosition.ofDegrees(90.0 * GEAR_RATIO)
private val ELBOW_DOWN = EncoderPosition.ofDegrees(0.0 * GEAR_RATIO)
private val ELBOW_MID = EncoderPosition.ofDegrees(45.0 * GEAR_RATIO)


class Robot(peripherals: Peripherals):  CompetitionRobot{
    var right_Drive = Motor(peripherals.takePort(1), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var left_Drive = Motor(peripherals.takePort(2), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var arm = Motor(peripherals.takePort(3), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var elbow = Motor(peripherals.takePort(4), Motor.Gearset.Red, Motor.Direction.REVERSE)
    var claw = Motor(peripherals.takePort(5), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var controller = peripherals.takeController(Controller.Id.Primary)
    override fun driverInit()  {
        elbow.target = Brake(BrakeMode.BRAKE)
        arm.target = Brake(BrakeMode.BRAKE)
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
                arm.target = MotorControl.Position(ARM_DOWN, ARM_SPEED)
                elbow.target = MotorControl.Position(ELBOW_DOWN, ARM_SPEED)
            }

            if (input.a.isNowPressed) {
                arm.target = MotorControl.Position(ARM_MID, ARM_SPEED)
                elbow.target = MotorControl.Position(ELBOW_MID, ARM_SPEED)
            }

            if (input.x.isNowPressed) {
                arm.target = MotorControl.Position(ARM_UP, ARM_SPEED)
                elbow.target = MotorControl.Position(ElBOW_UP, ARM_SPEED)
            }

        } catch (err: DeviceException) {
            throw RuntimeException(err)
        }
    }

    override fun autonomousInit() {
        arm.target = MotorControl.Position(ARM_UP, ARM_SPEED)
    }

}