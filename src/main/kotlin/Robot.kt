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
private val ARM_UP = EncoderPosition.ofDegrees(180.0 * GEAR_RATIO)
private val ARM_DOWN = EncoderPosition.ofDegrees(1.0 * GEAR_RATIO)

class Robot(peripherals: Peripherals):  CompetitionRobot{
    var left_Drive = Motor(peripherals.takePort(1), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var right_Drive = Motor(peripherals.takePort(0), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var arm = Motor(peripherals.takePort(2), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var claw = Motor(peripherals.takePort(3), Motor.Gearset.Red, Motor.Direction.FORWARD)
    var controller = peripherals.takeController(Controller.Id.Primary)
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
                claw.target = Voltage(6.0)
            } else if (input.l2.pressed) {
                claw.target = Voltage(-6.0)
            } else {
                claw.target = Brake(BrakeMode.HOLD)
            }

            if (input.r1.isNowPressed) {
                arm.target = MotorControl.Position(ARM_UP, ARM_SPEED)
            }

            if (input.l1.isNowPressed) {
                arm.target = MotorControl.Position(ARM_DOWN, ARM_SPEED)
            }

        } catch (err: DeviceException) {
            throw RuntimeException(err)
        }
    }

    override fun autonomousInit() {
        arm.target = MotorControl.Position(ARM_UP, ARM_SPEED)
    }

}