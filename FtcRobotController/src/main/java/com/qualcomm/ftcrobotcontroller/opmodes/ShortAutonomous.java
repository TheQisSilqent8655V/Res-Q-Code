package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Conno on 3/14/2016.
 */
public class ShortAutonomous extends OpMode{

    public static DcMotor BLWheel, BRWheel, FLWheel, FRWheel, LLift, RLift, Top;
    public static Servo LFront, RFront, Intake, ILift, Climber, HServo, AClimber;
    public static GyroSensor gyro;
    public static UltrasonicSensor ultra, eopd2;
    double bgh=0.0, ghm=0.0, drgh=0.0, gt=4.0, rwp=0.0, lwp=0.0, lp=0.0, lsp=0.0, flsp=0.2, frsp=0.7, hsp=0.76, ip=0.5, cp=0.86, bp=0.14, acp=0.25, su=0.0, ur=0.0;
    int davalue = 0, alliance = 1, delay = 0;
    boolean defense = true, startpos = true;
    Runnable calcDaGyro = new Runnable() {
        public void run() {
            bgh = gyro.getRotation();
            while(true) {
                ghm = (bgh - gyro.getRotation());
                if(ghm > -gt && ghm < gt)
                    ghm = 0;
                drgh += ghm;
                doDaSleep(10);
            }
        }
    };
    Thread DaGyroThread = new Thread(calcDaGyro);
    Runnable AutoPro = new Runnable() {
        public void run() {
            climbersAuton(delay, alliance, defense, startpos);
        }
    };
    Thread Autonomous = new Thread(AutoPro);
    Runnable LiftLift = new Runnable() {
        public void run() {
            lp = -0.6;
            doDaSleep(1750);
            lp = 0.0;
            bp = 0.58;
        }
    };
    Thread liftUntilSee = new Thread(LiftLift);
    @Override
    public void init() {
        BLWheel = hardwareMap.dcMotor.get("BL");
        BRWheel = hardwareMap.dcMotor.get("BR");
        FLWheel = hardwareMap.dcMotor.get("FL");
        FRWheel = hardwareMap.dcMotor.get("FR");
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
        gyro = hardwareMap.gyroSensor.get("G");
        ultra = hardwareMap.ultrasonicSensor.get("U");
        eopd2 = hardwareMap.ultrasonicSensor.get("E");
        RLift.setDirection(DcMotor.Direction.REVERSE);
        BRWheel.setDirection(DcMotor.Direction.REVERSE);
        FLWheel.setDirection(DcMotor.Direction.REVERSE);
        BLWheel.setDirection(DcMotor.Direction.REVERSE);
    }
    @Override
    public void init_loop() {
        if(gamepad1.a) {
            if (alliance == 1)
                alliance = 2;
            else
                alliance = 1;
            while(gamepad1.a)
                ;
        }
        if(gamepad1.b) {
            if(startpos == true)
                startpos = false;
            else
                startpos = true;
            while(gamepad1.b)
                ;
        }
        if(gamepad1.x) {
            if(defense == true)
                defense = false;
            else
                defense = true;
            while(gamepad1.x)
                ;
        }
        telemetry.addData("Defense - ", defense);
        if(gamepad1.dpad_up) {
            delay += 1;
            while(gamepad1.dpad_up)
                ;
        }
        if(gamepad1.dpad_down) {
            delay -= 1;
            while(gamepad1.dpad_down)
                ;
        }
        telemetry.addData("Delay - ", delay);
        if(alliance == 1)
            telemetry.addData("Alliance - ", "Red");
        else
            telemetry.addData("Alliance - ", "Blue");
        if(startpos == true)
            telemetry.addData("Start Position - ", "Close");
        else
            telemetry.addData("Start Position - ", "Far");
    }
    @Override
    public void loop() {
        if(davalue == 0) {
            DaGyroThread.start();
            Autonomous.start();
            davalue++;
        }
        runAllMotorsSlashServos();
        telemetry.addData("gyro - ", drgh);
        telemetry.addData("Start Ultra - ", su);
        telemetry.addData("Stop Ultra - ", ur);
    }
    void climbersAuton(int startdelay, int alliance, boolean defense, boolean position) {
        boolean red = (alliance == 1);
        doDaSleep(startdelay);
        liftUntilSee.start();
        if(position) {
            driveStraight(1, 0.3, 1800);
            bp = 0.58;
            turnGyroBasedOnAlliance(red, 0.3, 4000);
            driveStraight(1, 0.2, 1500);
            turnGyroBasedOnAlliance(red, 0.3, 9000);
        } else {
            driveStraight(1, 0.3, 3500);
            turnGyroBasedOnAlliance(red, 0.3, 4000);
            if(red)
                drgh = 9000;
            else
                drgh = -9000;
        }
        long beforetime = System.currentTimeMillis();
        while(ultraAverageValue(2, 10, 2) > 9) {
            lwp = 0.1;
            rwp = 0.1;
            if(System.currentTimeMillis() > beforetime + 5000) {
                driveStraight(-1, 0.2, 500);
                turnGyroBasedOnAlliance(red, 0.3, 4500);
                driveStraight(1, 0.2, 500);
                turnGyroBasedOnAlliance(red, 0.3, 9000);
                beforetime = System.currentTimeMillis();
            }
        }
        driveStraight(-1, 0.1, 100);
        turnGyroBasedOnAlliance(red, 0.45, 500);
        turnGyro(0.25, 0);
        driveStraight(-1, 0.2, 750);
        su = ultraAverageValue(5, 10, 1);
        boolean goingforward = true;
        ur = 0.0;
        beforetime = System.currentTimeMillis();
        while(goingforward) {
            singleDriveStraight(1, 0.1, 0);
            ur = ultraAverageValue(5, 5, 1);
            if(su < 30) {
                if(ur > 34) {
                    goingforward = false;
                }
            } else {
                if(ur < 30) {
                    goingforward = false;
                }
            }
            if(System.currentTimeMillis() > (beforetime + 3500)) {
                goingforward = false;
                beforetime = 0;
            }
        }
        if(beforetime == 0) {
            goingforward = true;
            while(goingforward) {
                singleDriveStraight(-1, 0.1, 0);
                ur = ultraAverageValue(5, 5, 1);
                if(ur < 34) {
                    goingforward = false;
                    su = 29;
                }
            }
        }
        lwp = 0.0;
        rwp = 0.0;
        acp = 0.45;
        if(red) {
            if(su < 30) {
                driveStraight(-1, 0.2, 0);
            } else {
                driveStraight(1, 0.2, 900);
            }
            turnGyro(0.3, 8500);
        } else {
            if(su < 30) {
                driveStraight(-1, 0.2, 400);
            } else {
                driveStraight(1, 0.2, 950);
            }
            turnGyro(0.3, -8500);
        }
        beforetime = System.currentTimeMillis();
        while(ultraAverageValue(2, 10, 2) > 9 || System.currentTimeMillis() > beforetime + 5000) {
            lwp = 0.15;
            rwp = 0.15;
        }
        for(double f = acp; f < 0.7655; f = f + 0.01) {
            acp = f;
            doDaSleep(10);
        }
        if(defense) {
            driveStraight(-1, 0.6, 2000);
            if(red)
                turnGyro(0.6, 1000);
            else
                turnGyro(0.6, 0);
            driveStraight(1, 0.6, 1750);
        }
    }
    double ultraAverageValue(int tests, int wait, int whichultra) {
        double ultravalue = 0.0;
        double fillervalue;
        for(int i = 0; i < tests; i++) {
            if(whichultra == 1) {
                fillervalue = ultra.getUltrasonicLevel();
            } else {
                fillervalue = eopd2.getUltrasonicLevel();
            }
            if(fillervalue > 3) {
                ultravalue += fillervalue;
            } else {
                i--;
            }
            doDaSleep(wait);
        }
        return (ultravalue / tests);
    }
    void turnGyro(double power, double gyroturnvalue) {
        boolean increasegyro = (drgh < gyroturnvalue);
        if(increasegyro) {
            while(drgh < gyroturnvalue) {
                lwp = power;
                rwp = -power;
            }
        } else {
            while(drgh > gyroturnvalue) {
                lwp = -power;
                rwp = power;
            }
        }
        lwp = 0.0;
        rwp = 0.0;
    }
    void turnGyroBasedOnAlliance(boolean red, double power, double gyrovalue) {
        if(red)
            turnGyro(power, gyrovalue);
        else
            turnGyro(power, -gyrovalue);
    }
    void driveStraight(int direction, double speed, int time) {
        double startingheading = drgh;
        speed = Range.clip(speed, 0.0, 0.9);
        long starttime = System.currentTimeMillis();
        long endtime = starttime + time;
        while(System.currentTimeMillis() < endtime) {
            if(startingheading > drgh + 30) {
                lwp = (direction) * (speed * 1.1);
                rwp = (direction) * speed;
            } else if(startingheading < drgh - 30) {
                lwp = (direction) * speed;
                rwp = (direction) * (speed * 1.1);
            } else {
                lwp = (direction) * speed;
                rwp = (direction) * speed;
            }
        }
        lwp = 0.0;
        rwp = 0.0;
    }
    void singleDriveStraight(int direction, double speed, double startgyro) {
        speed = Range.clip(speed, 0.0, 0.9);
        if(startgyro > drgh + 30) {
            lwp = (direction) * (speed * 1.1);
            rwp = (direction) * speed;
        } else if(startgyro < drgh - 30) {
            lwp = (direction) * speed;
            rwp = (direction) * (speed * 1.1);
        } else {
            lwp = (direction) * speed;
            rwp = (direction) * speed;
        }
    }
    void doDaSleep(int milli) {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException ex) {

        }
    }
    void runRightWheels(double power) {
        FRWheel.setPower(power);
        BRWheel.setPower(power);
    }
    void runLeftWheels(double power) {
        BLWheel.setPower(power);
        FLWheel.setPower(power);
    }
    void runLift(double power) {
        RLift.setPower(power);
        LLift.setPower(power);
    }
    void runLinearSlide(double power) {
        Top.setPower(power);
    }
    void runAllMotorsSlashServos() {
        runLift(lp);
        runRightWheels(rwp);
        runLeftWheels(lwp);
        runLinearSlide(lsp);
        Intake.setPosition(ip);
        LFront.setPosition(flsp);
        RFront.setPosition(frsp);
        ILift.setPosition(bp);
        Climber.setPosition(cp);
        HServo.setPosition(hsp);
        AClimber.setPosition(acp);
    }
} // Original was 738
