package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by Conno on 1/14/2016.
 */
public class AllAutonomousFile extends DE {

    //Sensor Variables
    double beforegyroheading = 0.0;
    double gyroheadingmaybe = 0.0;
    double darealgyroheading = 0.0;
    double gyrothreshold = 4.0;

    // Drive Variables
    double rightwheelpower = STOP_MOTOR_POWER;
    double leftwheelpower = STOP_MOTOR_POWER;

    // Lift Variables
    double liftpower = STOP_MOTOR_POWER;

    // Linear Slide Variables
    double linearslidepower = STOP_MOTOR_POWER;

    // Servo Variables
    double flservoposition = 0.2;
    double frservoposition = 0.7;
    double hookservoposition = 0.6655;
    double intakepower = 0.5;
    double climberposition = 0.55;
    double bucketposition = BUCKET_MIN_VAL;
    double aclimberposition = 0.3775;
    double wenchservoposition = 0.9;
    double jacksonthingservoposition = JACKSON_THING_MIN_VAL;
    double testultravalue = 0.0;

    // Thread Variables
    int davalue = 0;
    double startultra = 0.0;
    double ultrasonicreading = 0.0;

    // Autonomous Chooser Variables
    int alliance = RED;
    int delay = 0;
    boolean defense = true;
    boolean startpos = CLOSE;
    boolean dump = true;

    /*
     * This thread constantly calculates the gyro heading
     * adjust the gyrothreshold variable to toggle sensitivity
     */
    Runnable calcDaGyro = new Runnable() {
        public void run() {
            beforegyroheading = gyro.getRotation();
            while(true)
            {
                gyroheadingmaybe = (beforegyroheading - gyro.getRotation());

                // Eliminate really small values the gyro gets if it is still
                if(gyroheadingmaybe > -gyrothreshold && gyroheadingmaybe < gyrothreshold)
                {
                    gyroheadingmaybe = 0;
                }
                darealgyroheading += gyroheadingmaybe;
                doDaSleep(10);
            }
        }
    };
    Thread DaGyroThread = new Thread(calcDaGyro);

    /*
     * This is the main autonomous thread put autonomous functions in here to
     * run during autonomous
     */
    Runnable AutoPro = new Runnable() {
        public void run() {
            climbersAuton(delay, alliance, defense, startpos, dump);
        }
    };

    Thread Autonomous = new Thread(AutoPro);

    /*
     * This will lift the lift enough so that the ultrasonic can see
     * This will make the autonomous quicker by doing this while driving
     */
    Runnable LiftLift = new Runnable() {
        public void run() {
            liftpower = -0.6;
            doDaSleep(1850);
            liftpower = -0.1;
            bucketposition = BUCKET_MAX_VAL;
        }
    };

    Thread liftUntilSee = new Thread(LiftLift);

    /*
     * In the init loop we have the autonomous chooser that has the options to change
     * Which alliance you are on
     * Starting position
     * If you want to play defense after
     * Any delay you could want
     * Whether you want to dump the climbers or not
     */
    @Override
    public void init_loop() {
        // Alliance
        if(gamepad1.a)
        {
            if (alliance == RED)
                alliance = BLUE;
            else
                alliance = RED;

            while(gamepad1.a)
            {

            }
        }

        // Starting position
        if(gamepad1.b)
        {
            if(startpos == CLOSE)
                startpos = FAR;
            else
                startpos = CLOSE;

            while(gamepad1.b)
            {

            }
        }

        // Dumping option
        if(gamepad1.right_bumper)
        {
            if(dump)
                dump = false;
            else
                dump = true;

            while(gamepad1.right_bumper)
            {

            }
        }
        telemetry.addData("Dump ", dump);

        // Defense
        if(gamepad1.x)
        {
            if(defense == true)
                defense = false;
            else
                defense = true;

            while(gamepad1.x)
            {

            }
        }
        telemetry.addData("Defense - ", defense);

        // Starting delay
        if(gamepad1.dpad_up)
        {
            delay += 1;
            while(gamepad1.dpad_up)
            {

            }
        }

        if(gamepad1.dpad_down)
        {
            delay -= 1;
            while(gamepad1.dpad_down)
            {

            }
        }
        telemetry.addData("Delay - ", delay);
        // display everything else
        if(alliance == RED)
            telemetry.addData("Alliance - ", "Red");
        else
            telemetry.addData("Alliance - ", "Blue");

        if(startpos == CLOSE)
            telemetry.addData("Start Position - ", "Close");
        else
            telemetry.addData("Start Position - ", "Far");
    }

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
    @Override
    public void loop() {
        if(davalue == 0)
        {
            if(alliance == RED)
                climberposition = RED_MAX_SERVO_VALUE;
            else
                climberposition = BLUE_MIN_SERVO_VALUE;
            DaGyroThread.start();
            Autonomous.start();
            davalue++;
        }
        runAllMotorsSlashServos();
        telemetry.addData("Did Connor break the robot - ", didConnorBreakTheRobot());
        telemetry.addData("gyro - ", darealgyroheading);
        telemetry.addData("Start Ultra - ", startultra);
        telemetry.addData("Stop Ultra - ", ultrasonicreading);
        telemetry.addData("Test Ultra - ", testultravalue);
    }

    /*
     * This is the autonomous that will dump the climbers and have the option
     * to go into the opponents parking zone after and try to play defense
     * @param startdelay - the amount of time to wait before running in milliseconds
     * @param alliance - which alliance you are on, RED or BLUE
     * @param defense - decision to go into the opponents parking zone, true or false
     * @param position - the starting position CLOSE or FAR
     */
    void climbersAuton(int startdelay, int alliance, boolean defense, boolean position, boolean todump)
    {
        // Determine alliance color
        boolean red = (alliance == RED);

        // Delay if needed
        doDaSleep((startdelay * 1000));

        // Lift the lift until the ultrasonic can see past
        liftUntilSee.start();

        if(position)
        {
            // Drive forward a little and turn parallel to mountain
            // We do this to make it more consistent to position, might help that 0 for
            // the gyro is always parallel with the wall
            driveStraight(BACKWARD, 0.6, 1200);
            bucketposition = BUCKET_MAX_VAL;
            turnGyroBasedOnAlliance(red, 0.4, 4000);

            // Drive in front of mountain and turn to wall
            driveStraight(BACKWARD, 0.5, 1400);
            turnGyroBasedOnAlliance(red, 0.4, 9000);
        }
        else
        {
            // Drive forward past the mountain
            driveStraight(BACKWARD, 0.3, 3500);

            // Turn to wall and set the gyro value equal to what it would be for the other
            // position in order to have the same code for both positions
            turnGyroBasedOnAlliance(red, 0.4, 4000);
            if(red)
                darealgyroheading = 9000;
            else
                darealgyroheading = -9000;
        }

        long beforetime = System.currentTimeMillis();
        testultravalue = ultraAverageValue(2, 10, 2);

        // Go until close to wall and turn parallel
        while(testultravalue > (DISTANCE_TO_DUMP_CLIMBERS + 8))
        {
            if(red) {
                if (testultravalue > (DISTANCE_TO_DUMP_CLIMBERS + 30)) {
                    singleDriveStraight(BACKWARD, 0.2, 9000);
                } else {
                    singleDriveStraight(BACKWARD, 0.15, 9000);
                }
            }
            else {
                if (testultravalue > (DISTANCE_TO_DUMP_CLIMBERS + 30)) {
                    singleDriveStraight(BACKWARD, 0.2, -9000);
                } else {
                    singleDriveStraight(BACKWARD, 0.15, -9000);
                }
            }

            // If to much time passes before sensing wall try and reposition then try again
            if(System.currentTimeMillis() > beforetime + 5000)
            {
                driveStraight(FORWARD, 0.2, 500);
                turnGyroBasedOnAlliance(red, 0.3, 4500);
                driveStraight(BACKWARD, 0.2, 1500);
                turnGyroBasedOnAlliance(red, 0.3, 9000);
                beforetime = System.currentTimeMillis();
            }
            testultravalue = ultraAverageValue(2, 10, 2);
        }

        // Get some space from the wall and turn parallel
        driveStraight(FORWARD, 0.2, 100);
        turnGyroBasedOnAlliance(red, 0.45, 500);
        turnGyro(0.25, 0);

        // Make sure the robot is between the mountain and the beacon
        driveStraight(FORWARD, 0.2, 750);

        // Go straight along the wall until the beacon or until past beacon
        startultra = ultraAverageValue(5, 10, 1);
        boolean goingforward = true;
        ultrasonicreading = 0.0;
        beforetime = System.currentTimeMillis();

        // Keep driving forward until the robot hits the beacon
        while(goingforward)
        {
            singleDriveStraight(BACKWARD, 0.1, 0);
            ultrasonicreading = ultraAverageValue(5, 5, 1);

            // If robot starts in front of beacon
            if(startultra < 30)
            {
                if(ultrasonicreading > 34)
                {
                    goingforward = false;
                }
            }
            else // If robot doesn't start in front of beacon
            {
                if(ultrasonicreading < 30)
                {
                    goingforward = false;
                }
            }

            // Breaks the loop if too much time has passed
            if(System.currentTimeMillis() > (beforetime + 3500))
            {
                goingforward = false;
                beforetime = 0;
            }
        }

        // If it didn't detect beacon the first time
        if(beforetime == 0)
        {
            goingforward = true;
            while(goingforward)
            {
                singleDriveStraight(FORWARD, 0.1, 0);
                ultrasonicreading = ultraAverageValue(5, 5, 1);

                // Lessen the threshold to make sure it senses the beacon
                if(ultrasonicreading < 34)
                {
                    goingforward = false;
                    startultra = 29;
                }
            }
        }
        leftwheelpower = STOP_MOTOR_POWER;
        rightwheelpower = STOP_MOTOR_POWER;
        aclimberposition = 0.45; // For testing to see when it detects the beacon

        // Get into position and turn toward beacon
        if(red)
        {
            if(startultra < 30)
            {
                driveStraight(FORWARD, 0.2, 0);
            }
            else
            {
                driveStraight(BACKWARD, 0.2, 900);
            }
            turnGyro(0.55, 8500);
        }
        else
        {
            if(startultra < 30)
            {
                driveStraight(FORWARD, 0.2, 400);
            }
            else
            {
                driveStraight(BACKWARD, 0.2, 950);
            }
            turnGyro(0.55, -8500);
        }

        beforetime = System.currentTimeMillis();

        testultravalue = ultraAverageValue(2, 10, 2);

        // Go toward beacon until close enough and then dump climbers or until too much time had past
        while(testultravalue > (DISTANCE_TO_DUMP_CLIMBERS + 3)  || System.currentTimeMillis() > beforetime + 5000)
        {
            leftwheelpower = 0.15;
            rightwheelpower = 0.15;
            testultravalue = ultraAverageValue(2, 10, 2);
        }

        // Dump the climbers, to adjust speed change the sleep value
        if(todump)
        {
            for (double f = aclimberposition; f < 0.7655; f = f + 0.01) {
                aclimberposition = f;
                doDaSleep(10);
            }
        }

        // If playing defense run into other beacon zone
        if(defense)
        {
            driveStraight(FORWARD, 0.6, 2000);
            if(red)
                turnGyro(0.6, 1000);
            else
                turnGyro(0.6, 0);
            driveStraight(BACKWARD, 0.6, 1750);
        }
        else
        {
           // driveStraight(FORWARD, 0.6, 500);
            rightwheelpower = 0.0;
            leftwheelpower = 0.0;
        }
        rightwheelpower = 0.0;
        leftwheelpower = 0.0;
    }

    /*
     * This is the method that says if I broke the robot
     * This is the most accurate method here so far
     */
    boolean didConnorBreakTheRobot()
    {
        return true;
    }

    /*boolean redBeacon()
    {
        return (color.red() > 50);
    }*/

    /*
     * This function reads the ultrasonic sensor accurately through an average
     * of multiple readings, it also throws out the extraneous
     * @param tests - the amount of readings to take
     * @param wait - the time in milliseconds to wait in between each reading
     * @param whichultra - which ultrasonic sensor to call 1 for top, 2 for eopd2
     * @return - the average reading of the ultrasonic sensor
     */
    double ultraAverageValue(int tests, int wait, int whichultra)
    {
        double ultravalue = 0.0;
        double fillervalue;
        for(int i = 0; i < tests; i++)
        {
            if(whichultra == 1)
            {
                fillervalue = ultra.getUltrasonicLevel();
            }
            else
            {
                fillervalue = eopd2.getUltrasonicLevel();
            }
            if(fillervalue > 3)
            {
                ultravalue += fillervalue;
            }
            else
            {
                i--;
            }
            doDaSleep(wait);
        }
        return (ultravalue / tests);
    }

    /*
     * This will accurately turn the robot with the gyro
     * it can determine which direction it needs to turn based on
     * the gyro value to turn to
     * May want to include some type of pid
     * @param power - the speed the robot moves
     * @param gyroturnvalue - the gyro value to turn to
     */
    void turnGyro(double power, double gyroturnvalue)
    {
        boolean increasegyro = (darealgyroheading < gyroturnvalue);
        if(increasegyro)
        {
            while(darealgyroheading < gyroturnvalue)
            {
                leftwheelpower = power;
                rightwheelpower = -power;
            }
        }
        else
        {
            while(darealgyroheading > gyroturnvalue)
            {
                leftwheelpower = -power;
                rightwheelpower = power;
            }
        }
        leftwheelpower = 0.0;
        rightwheelpower = 0.0;
    }

    /*
     * Function for easy adjustments for turning based on the alliance
     * Made to make autonomous code simpler and shorter
     * @param red - boolean value for if you are on the red alliance
     * @param power - the speed to run the wheels at
     * @param gyrovalue - the value to turn to
     */
    void turnGyroBasedOnAlliance(boolean red, double power, double gyrovalue)
    {
        if(red)
            turnGyro(power, gyrovalue);
        else
            turnGyro(power, -gyrovalue);
    }

    /*
     * This will drive the robot in a straight line  for a specified
     * amount of time using the gyro
     * @param direction - the direction the robot is going, FORWARD or BACKWARD
     * @param speed - the power of the wheels
     * @param time - the time of the driving in milliseconds
     */
    void driveStraight(int direction, double speed, int time)
    {
        double startingheading = darealgyroheading;
        speed = Range.clip(speed, 0.0, 0.9);
        long starttime = System.currentTimeMillis();
        long endtime = starttime + time;
        while(System.currentTimeMillis() < endtime)
        {
            if(startingheading > darealgyroheading + GYRO_STRAIGHT_THRESHOLD)
            {
                leftwheelpower = (direction) * (speed * 1.1);
                rightwheelpower = (direction) * speed;
            }
            else if(startingheading < darealgyroheading - GYRO_STRAIGHT_THRESHOLD)
            {
                leftwheelpower = (direction) * speed;
                rightwheelpower = (direction) * (speed * 1.1);
            }
            else
            {
                leftwheelpower = (direction) * speed;
                rightwheelpower = (direction) * speed;
            }
        }
        leftwheelpower = 0.0;
        rightwheelpower = 0.0;
    }

    /*
     * This will set the motors to the correct power a single time for driving straight
     * this is to be used in a loop with another thing when you are going forward until
     * something has changes, normally a sensor picks up a specified value
     * @param direction - the direction the robot is going, DE.FORWARD or DE.BACKWARD
     * @param speed - the power of the wheels
     * @param startgyro - the gyro value you want to correct to
     */
    void singleDriveStraight(int direction, double speed, double startgyro)
    {
        speed = Range.clip(speed, 0.0, 0.9);
        if(startgyro > darealgyroheading + GYRO_STRAIGHT_THRESHOLD)
        {
            leftwheelpower = (direction) * (speed * 1.1);
            rightwheelpower = (direction) * speed;
        }
        else if(startgyro < darealgyroheading - GYRO_STRAIGHT_THRESHOLD)
        {
            leftwheelpower = (direction) * speed;
            rightwheelpower = (direction) * (speed * 1.1);
        }
        else
        {
            leftwheelpower = (direction) * speed;
            rightwheelpower = (direction) * speed;
        }
    }

    /*
     * This is the function that runs in the main loop that controls the motors and servos with
     * the powers and positions that are passed to it by the autonomous thread
     */
    void runAllMotorsSlashServos()
    {
        // Run the motors
        runLift(liftpower);
        runRightWheels(rightwheelpower);
        runLeftWheels(leftwheelpower);
        runLinearSlide(linearslidepower);

        // Run the servos
        Intake.setPosition(intakepower);
        LFront.setPosition(flservoposition);
        RFront.setPosition(frservoposition);
        ILift.setPosition(bucketposition);
        Climber.setPosition(climberposition);
        HServo.setPosition(hookservoposition);
        AClimber.setPosition(aclimberposition);
        Wench.setPosition(wenchservoposition);
        JacksonThing.setPosition(jacksonthingservoposition);
    }
} // AllAutonomousFile
