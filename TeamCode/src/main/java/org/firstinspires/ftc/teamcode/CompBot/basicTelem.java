package org.firstinspires.ftc.teamcode.CompBot;

import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.STOP_AND_RESET_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.ODO.GoBildaPinpointDriver;

@TeleOp(name = "basic telemetry", group = "CompBot")
public class basicTelem extends LinearOpMode {
    /*
    The point of this code is to be a basic telem that works for thursday
    using different classes already made and is not tested as of creation 10/21/24
    date last updated and tested: 10/21/24
    uses clawSlideImport and permaIntake
    */

    DcMotor FLMotor, BLMotor, BRMotor, FRMotor, pivot, slide;

    Servo FLServo, BLServo, BRServo, FRServo, intakeL, wrist;

    GoBildaPinpointDriver odo;


    ElapsedTime turnTime = new ElapsedTime();


    // In case builders are bad, is offset center for servo
    double FLServoOffSet = .00;     //0.00
    double FRServoOffSet = .00;     //0.00
    double BLServoOffSet = .00;     //0.01
    double BRServoOffSet = .00;     //.007


    static double TRACKWIDTH = 14; //in inches
    static double WHEELBASE = 15; //in inches


    public void runOpMode() throws InterruptedException {

        initRobot(); // Does all the robot stuff

        /**
         * controls for game pad 1:
         * right trigger: forwards
         * left trigger: backwards
         * right stick x: rotate
         * left stick x: strafe
         *
         *
         * controls for game pad 2:
         * left stick y: in and out of arm
         * right stick y: up and down of arm
         * a: constant in changed to out while button is being held
         * while b is being held right stick x: wrist position
         * presets for:
         * attaching clip to sample
         * attaching specimen(clip + sample) to top rung
         * presets for bucket 1 and 2
         */

        waitForStart();
        while (opModeIsActive()) {

            //game pad 1
            double speedGMP1 = gamepad1.left_trigger - gamepad1.right_trigger; // Makes it so that the triggers cancel each other out if both are pulled at the same time
            double angleGMP1 = -gamepad1.right_stick_x;

            if (speedGMP1 != 0) move(gamepad1.left_stick_x, speedGMP1);
            else if (angleGMP1 != 0) rotate(angleGMP1);
            else {
                FLMotor.setPower(0);
                BLMotor.setPower(0);
                BRMotor.setPower(0);
                FRMotor.setPower(0);
            }


            //game pad 2 inputs
            double armLength = -gamepad2.right_stick_y;
            double armAngle = -gamepad2.left_stick_y;

            //call move arm
            extendArm(armLength);
            liftArm(armAngle);

            //claw intake/outtake
            if (gamepad2.a) {
                intakeL.setPosition(1);
            } else {
                intakeL.setPosition(0);
            }

            //wrist rotation
            if (gamepad2.b) {
                double x;
                if (wrist.getPosition() >= .1)
                    x = 0;
                else
                    x = .7;
                wrist.setPosition(x);
            }
        }
    }


    // to lift arm, input from game pad 2 straight in
    public void liftArm(double x) {
        if (pivot.getCurrentPosition() >= 2500 && x > 0) {
            x = 0;
        } else if (pivot.getCurrentPosition() <= -2500 && x < 0) {
            x = 0;
        }
        pivot.setPower(x);
        telemetry.addData("lift power: ", pivot.getPower());
    }

    // to extend arm, input from game pad 2 straight in
    public void extendArm(double x) {
        if (slide.getCurrentPosition() >= 4750 && x > 0) {
            x = 0;
        } else if (slide.getCurrentPosition() <= -4750 && x < 0) {
            x = 0;
        }
        slide.setPower(x);
        telemetry.addData("slide power: ", slide.getPower());
    }


    /**
     * Initializes the robot.<br>
     * Starts all the devices and maps where they go
     * As well as sets direction and whether motors run with encoders or not
     */
    public void initRobot() {

        // Maps the motor objects to the physical ports
        FLMotor = hardwareMap.get(DcMotor.class, "FLMotor");
        BLMotor = hardwareMap.get(DcMotor.class, "BLMotor");
        FRMotor = hardwareMap.get(DcMotor.class, "FRMotor");
        BRMotor = hardwareMap.get(DcMotor.class, "BRMotor");

        // Sets the encoder mode
        FLMotor.setMode(RUN_USING_ENCODER);
        BLMotor.setMode(RUN_USING_ENCODER);
        FRMotor.setMode(RUN_USING_ENCODER);
        BRMotor.setMode(RUN_USING_ENCODER);

        // Sets what happens when no power is applied to the motors.
        // In this mode, the computer will short the 2 leads of the motor, and because of math, the motor will be a lot harder to turn
        FLMotor.setZeroPowerBehavior(BRAKE);
        BLMotor.setZeroPowerBehavior(BRAKE);
        FRMotor.setZeroPowerBehavior(BRAKE);
        BRMotor.setZeroPowerBehavior(BRAKE);

        FLMotor.setDirection(REVERSE);
        BLMotor.setDirection(REVERSE);
        FRMotor.setDirection(REVERSE);
        BRMotor.setDirection(FORWARD);


        // Maps the servo objects to the physical ports
        FLServo = hardwareMap.get(Servo.class, "FLServo");
        BLServo = hardwareMap.get(Servo.class, "BLServo");
        FRServo = hardwareMap.get(Servo.class, "FRServo");
        BRServo = hardwareMap.get(Servo.class, "BRServo");

        // Sets the ends of the servos. Hover cursor over function for more info
        // Will need to be tuned later
        FLServo.scaleRange(FLServoOffSet, 1.0 + FLServoOffSet * 2);
        BLServo.scaleRange(BLServoOffSet, 1.0 + BLServoOffSet * 2);
        FRServo.scaleRange(FRServoOffSet, 1.0 + FRServoOffSet * 2);
        BRServo.scaleRange(BRServoOffSet, 1.0 + BRServoOffSet * 2);

        FLServo.setPosition(0.50 + FLServoOffSet);
        BLServo.setPosition(0.50 + BLServoOffSet);
        FRServo.setPosition(0.50 + FRServoOffSet);
        BRServo.setPosition(0.50 + BRServoOffSet);


        // Init GoBilda Pinpoint module
        odo = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
        odo.resetPosAndIMU();
        odo.setOffsets(177.8, 50.8);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);


        // Init slaw, claw, and pivot
        pivot = hardwareMap.dcMotor.get("pivot");
        slide = hardwareMap.dcMotor.get("slide");

        pivot.setMode(RUN_USING_ENCODER);
        slide.setMode(RUN_USING_ENCODER);

        pivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        slide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        pivot.setDirection(REVERSE);
        slide.setDirection(FORWARD);

        pivot.setPower(0);
        slide.setPower(0);


        //servos for intake
        intakeL = hardwareMap.get(Servo.class, "intake");
        wrist = hardwareMap.get(Servo.class, "wrist");

        intakeL.setDirection(Servo.Direction.FORWARD);
        wrist.setDirection(Servo.Direction.FORWARD);

        wrist.setPosition(0);
    }


    /**
     * Converts standard cartesian coordinates to polar coordinates
     *
     * @param x X input
     * @param y Y input
     * @return Returns an array of two doubles,<br>
     * <p>
     * [0] = R - Magnitude<br>
     * [1] = Theta Angle of input coordinate.<br>
     * Relative to unit circle, where 0deg in is to the right, 90 is up and 180 is left.
     * @see #polarToCartesian(double, double)
     */
    public double[] cartesianToPolar(double x, double y) {
        double[] arrayToReturn = new double[2];
        arrayToReturn[0] = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); // Radius
        arrayToReturn[1] = Math.atan(y / x) * (Math.PI / 180); // Theta


        return arrayToReturn;
    }


    /**
     * Converts polar coordinates to cartesian
     *
     * @param r     Magnitude of input coordinate
     * @param theta Angle of input coordinate.<br>
     *              Relative to unit circle, where 0deg in is to the right, 90 is up and 180 is left.
     * @return Returns an array of two doubles,<br>
     * <p>
     * [0] = X<br>
     * [1] = Y
     * @see #cartesianToPolar(double, double)
     */
    public double[] polarToCartesian(double r, double theta) {
        double[] arrayToReturn = new double[2];
        arrayToReturn[0] = r * Math.cos(theta); // X
        arrayToReturn[1] = r * Math.sin(theta); // Y

        return arrayToReturn;
    }


    /**
     * Moves the robot based on desired heading and power.<br>
     * Does not change the rotation of the robot at all
     *
     * @param heading Desired heading of the robot.<br>
     *                0 is straight forward 1 is fully right, -1 is fully left
     * @param power   Desired power to run the motors at
     */
    public void move(double heading, double power) {
        heading = (heading + 1) / 2;

        FLServo.setPosition(heading + FLServoOffSet);
        BLServo.setPosition(heading + BLServoOffSet);
        BRServo.setPosition(heading + BRServoOffSet);
        FRServo.setPosition(heading + FRServoOffSet);

        FLMotor.setPower(power);
        BLMotor.setPower(power);
        BRMotor.setPower(power);
        FRMotor.setPower(power);
    }


    /**
     * Rotates the robot around a center point.<br>
     * Does not move the robot in any other direction.<br>
     *
     * @param power Power to turn the robot at
     */
    public void rotate(double power) {

        // Set wheels for rotation
        FLServo.setPosition(.75 + .125 / 2);
        BLServo.setPosition(.25 - .125 / 2);
        BRServo.setPosition(.25 + .125 / 2);
        FRServo.setPosition(.75 - .125 / 2);

        //turn motors to rotate robot
        power = (power + 1) / 2;
        FLMotor.setPower(power);
        BLMotor.setPower(power);
        BRMotor.setPower(power);
        FRMotor.setPower(power);
    }


    /**
     * TODO this needs to be worked on nothing is done here
     *
     * <p>
     * Wheel angle is perpendicular to turn angle
     * turn angle is inverse tan(get angle) of 7.5(half of wheel base length) / (turning distance/2)
     * because it is radius of point we are trying to rotate around
     * speed = -1 to 1
     * turnRad = -1 to 1
     * turnDir = LEFT or RIGHT
     */
    public void moveAndRotate(double speed, double turnAmount) {

        double i_WheelAngle = Math.atan2(WHEELBASE, turnAmount - TRACKWIDTH / 2);
        double outsideAng = Math.atan2(WHEELBASE, turnAmount + TRACKWIDTH / 2);


    }


    /**
     * TODO Possibly make this not run at full speed at all times by adding some sort of power input
     * Uses the IMU to move the robot to face forward
     * <p>
     * As long as this function is called, it will try to rotate back to facing forward
     */
    public void rotateToCenter() {
        double orientation = odo.getHeading();
        telemetry.addData("Yaw angle", orientation);

        rotate(orientation);
    }


    /**
     * TODO All of this
     * Moves the robot to the detected specimen
     */
    public void moveToSpecimen() {

    }


    /**
     * TODO All of this as well
     * Moves the robot back to the storage area
     */
    public void moveToStore() {

    }

}