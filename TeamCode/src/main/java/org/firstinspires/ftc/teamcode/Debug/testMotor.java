package org.firstinspires.ftc.teamcode.Debug;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_WITHOUT_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "test motors", group = "debug")
public class testMotor extends LinearOpMode {

    DcMotor motor;

    public void runOpMode() {
        initR();

        waitForStart();

        while (opModeIsActive()) {
            double pow = -gamepad1.right_stick_y;
            motor.setPower(pow);
        }
    }

    public void initR() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        motor.setZeroPowerBehavior(BRAKE);
        motor.setDirection(FORWARD);
        motor.setMode(RUN_WITHOUT_ENCODER);
    }
}
