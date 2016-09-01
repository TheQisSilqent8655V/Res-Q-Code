package com.qualcomm.ftcrobotcontroller.opmodes;
import android.util.Log;

import com.qualcomm.robotcore.util.Range;

/**
 * Created by Conno on 1/2/2016.
 */
public class CompetitionTeleOp extends DE {

    // Lift Variables
    double liftpower = STOP_MOTOR_POWER;

    // Drive Variables
    double rightdrivepower = STOP_MOTOR_POWER;
    double leftdrivepower = STOP_MOTOR_POWER;

    // Top Thing Variables
    double topthingpower = STOP_MOTOR_POWER;

    // Servo Variables
    double intakepower = CONTINUOUS_SERVO_STOP;
    double climberposition = 0.8655;
    double leftfrontservovalue = 0.2;
    double rightfrontservovalue = 0.7;
    double bucketposition = 0.614;
    double hookservoposition = MIN_HOOK_SERVO_VALUE;
    boolean redalliance = true;
    double aclimberposition = 0.34;
    double wenchservoposition = WENCH_MAX_VAL;
    double jacksonthingservoposition = JACKSON_THING_MIN_VAL;

    String wtfishappening = "nothing";

    // Thread Variables
    int daValue = 0;
    String whatarestevensjoystickscontrolling = "The Wheels";


    /*
     * This is the thread that changes the joysticks control between the drive and the top motors
     * that control the linear slide. This is for controller 1(usually Steven)
     */
    Runnable changeStevenControl = new Runnable() {
        public void run() {
            while(true) {
                if(gamepad1.a)
                {
                    if(whatarestevensjoystickscontrolling.equals("The Wheels"))
                    {
                        whatarestevensjoystickscontrolling = "The Linear Slide";
                    }
                    else
                    {
                        whatarestevensjoystickscontrolling = "The Wheels";
                    }
                    while(gamepad1.a)
                    {

                    }
                }
            }
        }
    };

    Thread IMU = new Thread(changeStevenControl);

    @Override
    public void init() {

        BLWheel = hardwareMap.dcMotor.get("BL");
        BRWheel = hardwareMap.dcMotor.get("BR");
        //FLWheel = hardwareMap.dcMotor.get("FL");
        //FRWheel = hardwareMap.dcMotor.get("FR");
        LLift = hardwareMap.dcMotor.get("LL");
        RLift = hardwareMap.dcMotor.get("RL");
        Top = hardwareMap.dcMotor.get("LT");

        LFront = hardwareMap.servo.get("LF");
        RFront = hardwareMap.servo.get("RF");
        Intake = hardwareMap.servo.get("I");
        ILift = hardwareMap.servo.get("IL");
        Climber = hardwareMap.servo.get("C");
        HServo = hardwareMap.servo.get("H");
        AClimber = hardwareMap.servo.get("AC");

    }

    /*
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
    @Override
    public void init_loop() {
        if(gamepad1.a)
        {
            redalliance = !redalliance;
            while(gamepad1.a)
            {

            }
        }
        telemetry.addData("Red Alliance - ", redalliance);
    }

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
    @Override
    public void loop() {
        telemetry.addData("Left Front Servo - ", leftfrontservovalue);
        telemetry.addData("Right Front Servo - ", rightfrontservovalue);
        telemetry.addData("Bucket Servo - ", bucketposition);
        telemetry.addData("Climber Servo - ", climberposition);
        telemetry.addData("Intake Power - ", intakepower);
        telemetry.addData("Hook Servo - ", hookservoposition);
        telemetry.addData("Steven's Joysticks - ", whatarestevensjoystickscontrolling);
        telemetry.addData("Top Power", topthingpower);
        telemetry.addData("AClimberposition", aclimberposition);
        telemetry.addData("Wench", wenchservoposition);
        telemetry.addData("Jackson Thing", jacksonthingservoposition);
        telemetry.addData("Lift Power ", liftpower);
        telemetry.addData("Button", wtfishappening);
        //RLift.setPower(0.0);
        //LLift.setPower(0.0);
        //controlLift();
        if(daValue == 0)
        {
            if(redalliance)
                climberposition = RED_MAX_SERVO_VALUE;
            else
                climberposition = BLUE_MIN_SERVO_VALUE;
            bucketposition = BUCKET_MAX_VAL;
            IMU.start();
            daValue++;
        }
        twoControllerTeleOp();
    }

    /*
     * This controls the Lift for competition
     * Right trigger - down full speed
     * Right bumper - up full speed
     * Left trigger - down slow speed
     * Left - up full speed
     */
    void controlLift()
    {
        try {
            if (gamepad2.right_bumper) {
                liftpower = MIN_MOTOR_POWER; // This goes max speed up
                wtfishappening = "right bumper";
            } /*else if (gamepad2.right_trigger > 0) {
                liftpower = 0.75;
                wtfishappening = "right trigger";
            } */else if (gamepad2.left_bumper) {
                liftpower = SLOW_LIFT_DOWN_VAL;
                wtfishappening = "left bumper";
            } /*else if (gamepad2.left_trigger > 0) {
                liftpower = SLOW_LIFT_DOWN_VAL;
                wtfishappening = "left trigger";
            }  else if (gamepad2.a) {
                liftpower = SLOW_LIFT_DOWN_VAL;
                wtfishappening = "a button";
            } */ else {
                liftpower = STOP_MOTOR_POWER;
                wtfishappening = "nothing";
            }
            runLift(liftpower);
        } catch (IllegalArgumentException ex)
        {
            telemetry.addData("Lift Error - ",ex);
        }
    }

    /*
     * This controls the driving for two controller drive
     * Includes fast and slow drives
     * Right stick - right wheels
     * Left stick - left wheels
     * Right bumper - turbo
     * Right trigger - slow
     */
    void twoControllerDrive()
    {
        if(whatarestevensjoystickscontrolling.equals("The Wheels"))
        {
            if (gamepad1.right_bumper) {
                rightdrivepower = (gamepad1.left_stick_y + gamepad1.left_stick_x);
                leftdrivepower = (gamepad1.left_stick_y - gamepad1.left_stick_x);
            } else if (gamepad1.right_trigger > 0) {
                rightdrivepower = (((gamepad1.left_stick_y + gamepad1.left_stick_x)) * SLOW_DRIVE_VALUE);
                leftdrivepower = (((gamepad1.left_stick_y - gamepad1.left_stick_x)) * SLOW_DRIVE_VALUE);
            } else {
                rightdrivepower = (((gamepad1.left_stick_y + gamepad1.left_stick_x)) * REGULAR_DRIVE_VALUE);
                leftdrivepower = (((gamepad1.left_stick_y - gamepad1.left_stick_x)) * REGULAR_DRIVE_VALUE);
            }
            rightdrivepower = Range.clip(rightdrivepower, -1.0, 1.0);
            leftdrivepower = Range.clip(leftdrivepower, -1.0, 1.0);
            runRightWheels(rightdrivepower);
            runLeftWheels(leftdrivepower);
        }
        else
        {
            runRightWheels(STOP_MOTOR_POWER);
            runLeftWheels(STOP_MOTOR_POWER);
        }
    }

    /*
     * This runs the top linear slide only using one motor and the
     * left joystick
     * Uses the same slow and turbo buttons as the drive
     */
    void newRunTopThing()
    {
        if (whatarestevensjoystickscontrolling.equals("The Linear Slide")) {
            if (gamepad1.right_bumper) {
                topthingpower = (gamepad1.left_stick_y);
            } else if (gamepad1.right_trigger > 0) {
                topthingpower = ((gamepad1.left_stick_y) * SLOW_DRIVE_VALUE);
            } else {
                topthingpower = ((gamepad1.left_stick_y) * REGULAR_DRIVE_VALUE);
            }
            Top.setPower(topthingpower);
        } else {
            Top.setPower(STOP_MOTOR_POWER);
        }
    }

    /*
     * This will run the servo used to extend the hook for getting to the high zone
     * gamepad 1 dpad down - extend hooks
     * gamepad 1 dpad up - lower hooks
     */
    void runHookServo()
    {
        if(gamepad1.dpad_up)
        {
            hookservoposition += SERVO_INCREMENT_VALUE;
        }
        if(gamepad1.dpad_down)
        {
            hookservoposition -= SERVO_INCREMENT_VALUE;
        }
        hookservoposition = Range.clip(hookservoposition, MIN_HOOK_SERVO_VALUE, MAX_HOOK_SERVO_VALUE);
    HServo.setPosition(hookservoposition);
}

    /*
     * This will run the servo to engage the wench
     * gamepad 2 right joystick up - increase value
     * gamepad 2 right joystick down - decrease value
     */
    void runWenchServo()
    {
        if(gamepad2.right_stick_y > 0.1)
        {
            wenchservoposition += (SERVO_INCREMENT_VALUE * 2);
        }
        if(gamepad2.right_stick_y < -0.1)
        {
            wenchservoposition -= (SERVO_INCREMENT_VALUE * 2);
        }
        wenchservoposition = Range.clip(wenchservoposition, WENCH_MIN_VAL, WENCH_MAX_VAL);
        Wench.setPosition(wenchservoposition);
    }

    void runJacksonThingServo()
    {
        if(gamepad2.left_stick_y > 0.1)
        {
            jacksonthingservoposition += (SLIGHT_SERVO_INCREMENT_VALUE * 3);
        }
        if(gamepad2.left_stick_y < -0.1)
        {
            jacksonthingservoposition -= (SLIGHT_SERVO_INCREMENT_VALUE * 3);
        }
        jacksonthingservoposition = Range.clip(jacksonthingservoposition, JACKSON_THING_MIN_VAL, JACKSON_THING_MAX_VAL);
        JacksonThing.setPosition(jacksonthingservoposition);
    }

    /*
     * This controls the front servos for attaching to the churros
     * made it available for both drivers
     */
    void controlFrontServoThings() {
        if (gamepad1.b) {
            leftfrontservovalue = LEFT_FRONT_SERVO_OUT;
            rightfrontservovalue = RIGHT_FRONT_SERVO_OUT;
        } else if (gamepad1.y) {
            leftfrontservovalue = LEFT_FRONT_SERVO_DOWN;
            rightfrontservovalue = RIGHT_FRONT_SERVO_DOWN;
        } else if (gamepad1.x) {
            leftfrontservovalue = LEFT_FRONT_SERVO_UP;
            rightfrontservovalue = RIGHT_FRONT_SERVO_UP;
        } else if(gamepad2.dpad_up)
        {
            leftfrontservovalue = LEFT_FRONT_SERVO_UP;
            rightfrontservovalue = RIGHT_FRONT_SERVO_UP;
        } else if(gamepad2.dpad_down)
        {
            leftfrontservovalue = LEFT_FRONT_SERVO_DOWN;
            rightfrontservovalue = RIGHT_FRONT_SERVO_DOWN;
        } else if(gamepad2.dpad_left || gamepad2.dpad_right)
        {
            leftfrontservovalue = LEFT_FRONT_SERVO_OUT;
            rightfrontservovalue = RIGHT_FRONT_SERVO_OUT;
        }
        leftfrontservovalue = Range.clip(leftfrontservovalue, MIN_SERVO_VALUE, MAX_SERVO_VALUE);
        rightfrontservovalue = Range.clip(rightfrontservovalue, MIN_SERVO_VALUE, MAX_SERVO_VALUE);
        LFront.setPosition(leftfrontservovalue);
        RFront.setPosition(rightfrontservovalue);
    }

    /*
     * This runs the vex motor intake
     * The vex motor has trouble running at 1.0 and 0.0 and we also tried .99 and .01 and it
     * didn't move at these values this is why the cutoff is 0.1 and 0.9
     * With more testing we might be able to increase the speed by 3% to 7%, but that might
     * not be needed
     */
    void controlIntake()
    {
        if(gamepad1.left_bumper)
        {
            intakepower  += CONTINUOUS_SERVO_INCREMENT_VALUE;
        } else if(gamepad1.left_trigger > 0)
        {
            intakepower -=  CONTINUOUS_SERVO_INCREMENT_VALUE;
        }
        else
        {
            intakepower = CONTINUOUS_SERVO_STOP;
        }
        intakepower = Range.clip(intakepower, INTAKE_OUT_SPEED, INTAKE_IN_SPEED);
        Intake.setPosition(intakepower);
    }

    /**
     * This runs the bucket servo at a low speed by incrementing position
     */
    void controlBucket()
    {
        if(gamepad2.b)
        {
            bucketposition += SERVO_INCREMENT_VALUE;
        }
        if(gamepad2.a)
        {
            bucketposition -= SERVO_INCREMENT_VALUE;
        }
        bucketposition = Range.clip(bucketposition, BUCKET_MIN_VAL, BUCKET_MAX_VAL);
        ILift.setPosition(bucketposition);
    }

    /*
     * This runs the climber depositor slowly by incrementing position
     */
    void controlClimbers()
    {
        if (gamepad2.x)
        {
            if(redalliance)
                climberposition += SERVO_INCREMENT_VALUE;
            else
                climberposition -= SERVO_INCREMENT_VALUE;
        }
        if (gamepad2.y)
        {
            if(redalliance)
                climberposition -= SERVO_INCREMENT_VALUE;
            else
                climberposition += SERVO_INCREMENT_VALUE;
        }
        if(redalliance)
            climberposition = Range.clip(climberposition, RED_MIN_SERVO_VALUE, RED_MAX_SERVO_VALUE);
        else
            climberposition = Range.clip(climberposition, BLUE_MIN_SERVO_VALUE, BLUE_MAX_SERVO_VALUE);
        Climber.setPosition(climberposition);
    }

    /*
     * This controls the climber depositor that is mainly used in autonomous
     * Most likely will not use this in teleOp, but have it just in case it is out of position
     */
    void controlAutoClimbers()
    {
        if(gamepad1.dpad_left)
        {
            aclimberposition += SERVO_INCREMENT_VALUE;
        }
        if(gamepad1.dpad_right)
        {
            aclimberposition -= SERVO_INCREMENT_VALUE;
        }
        aclimberposition = Range.clip(aclimberposition, ACLIMBER_MIN_VAL, ACLIMBER_MAX_VAL);
        AClimber.setPosition(aclimberposition);
    }

    /*
     * Main function for competition
     */
    void twoControllerTeleOp() {
        //controlBucket();
        //controlFrontServoThings();
        //controlIntake();
        twoControllerDrive();
        //newRunTopThing();
        controlLift();
        //controlClimbers();
        //runHookServo();
        //controlAutoClimbers();
        //runWenchServo();
        //runJacksonThingServo();
    }
} // CompetitionTeleOp
