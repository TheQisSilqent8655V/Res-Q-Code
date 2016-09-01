package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Conno on 6/1/2016.
 */
public class OakRidgeTeleOp extends OpMode {

    DcMotor BLW;
    DcMotor BRW;
    DcMotor FLW;
    DcMotor FRW;
    DcMotor Intake;

    double rightdrivepower = 0.0;
    double leftdrivepower = 0.0;

    @Override
    public void init() {
        BLW = hardwareMap.dcMotor.get("BLW");
        BRW = hardwareMap.dcMotor.get("BRW");
        FLW = hardwareMap.dcMotor.get("FLW");
        FRW = hardwareMap.dcMotor.get("FRW");
        Intake = hardwareMap.dcMotor.get("I");
    }

    /*
       * Code to run when the op mode is first enabled goes here
       * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
       */
    @Override
    public void init_loop() {

    }

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
    @Override
    public void loop() {
        singleJoystickDrive();
        runIntake();
    }

    void runRightWheels(double power)
    {
        power = Range.clip(power, -1.0, 1.0);
        BRW.setPower(power);
        FRW.setPower(power);
    }

    void runLeftWheels(double power)
    {
        power = Range.clip(power, -1.0, 1.0);
        BLW.setPower(power);
        FLW.setPower(power);
    }

    void singleJoystickDrive()
    {
        rightdrivepower = (gamepad1.left_stick_y + gamepad1.left_stick_x);
        leftdrivepower = (gamepad1.left_stick_y - gamepad1.left_stick_x);
        runRightWheels(rightdrivepower);
        runLeftWheels(leftdrivepower);
    }

    void runIntake()
    {
        if(gamepad1.a)
        {
            Intake.setPower(0.5);
        }
        else if(gamepad1.b)
        {
            Intake.setPower(-0.5);
        }
        else
        {
            Intake.setPower(0.0);
        }

    }
}
