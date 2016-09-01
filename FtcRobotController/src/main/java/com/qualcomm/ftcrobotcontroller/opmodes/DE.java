package com.qualcomm.ftcrobotcontroller.opmodes;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.GyroSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.UltrasonicSensor;

/**
 * Created by Conno on 2/16/2016.
 */
public class DE extends OpMode {

    //Define Motors
    public static DcMotor BLWheel;
    public static DcMotor BRWheel;
    public static DcMotor FLWheel;
    public static DcMotor FRWheel;
    public static DcMotor LLift;
    public static DcMotor RLift;
    public static DcMotor Top;

    // Define Servos
    public static Servo LFront;
    public static Servo RFront;
    public static Servo Intake;
    public static Servo ILift;
    public static Servo Climber;
    public static Servo HServo;
    public static Servo AClimber;
    public static Servo Wench;
    public static Servo JacksonThing;

    // Define Sensors
    public static GyroSensor gyro;
    public static UltrasonicSensor ultra;
    public static UltrasonicSensor eopd2;

    //////// Autonomous Constants \\\\\\\\

    public static final int RED = 1;
    public static final int BLUE = 2;
    public static final int FORWARD = -1;
    public static final int BACKWARD = 1;
    public static final boolean CLOSE = true;
    public static final boolean FAR = false;
    public static final int GYRO_STRAIGHT_THRESHOLD = 30;
    public static final double DISTANCE_TO_DUMP_CLIMBERS = 9;



    //////// TeleOp Constants \\\\\\\\

    // General Motor
    public static final double MAX_MOTOR_POWER = 1.0;
    public static final double STOP_MOTOR_POWER = 0.0;
    public static final double MIN_MOTOR_POWER = -1.0;

    // Lift
    public static final double SLOW_LIFT_UP_VAL = -0.5;
    public static final double SLOW_LIFT_DOWN_VAL = 0.1;
    public static final double FAST_LIFT_VAL = 0.75;

    // Drive
    public static final double REGULAR_DRIVE_VALUE = 0.7;
    public static final double SLOW_DRIVE_VALUE = 0.2;

    // General Servo
    public static final double SERVO_INCREMENT_VALUE = 0.0025;
    public static final double SLIGHT_SERVO_INCREMENT_VALUE = 0.0005;
    public static final double CONTINUOUS_SERVO_INCREMENT_VALUE = 0.01;
    public static final double MIN_SERVO_VALUE = 0.0;
    public static final double MAX_SERVO_VALUE = 1.0;
    public static final double BLUE_MIN_SERVO_VALUE = 0.08;
    public static final double BLUE_MAX_SERVO_VALUE = 0.495;
    public static final double RED_MIN_SERVO_VALUE = 0.5505;
    public static final double RED_MAX_SERVO_VALUE = 1.0;
    public static final double CONTINUOUS_SERVO_STOP = 0.5;

    // Hook Servo
    public static final double MIN_HOOK_SERVO_VALUE = 0.01;
    public static final double MAX_HOOK_SERVO_VALUE = 0.7;

    // Front Servo
    public static final double LEFT_FRONT_SERVO_UP = 0.75;
    public static final double RIGHT_FRONT_SERVO_UP = 0.2;
    public static final double LEFT_FRONT_SERVO_DOWN = 0.3;
    public static final double RIGHT_FRONT_SERVO_DOWN = 0.64;
    public static final double LEFT_FRONT_SERVO_OUT = 0.15;
    public static final double RIGHT_FRONT_SERVO_OUT = 0.85;

    // Intake
    public static final double INTAKE_OUT_SPEED = 0.1;
    public static final double INTAKE_IN_SPEED = 0.9;

    // Bucket
    public static final double BUCKET_MIN_VAL = 0.2825;
    public static final double BUCKET_MAX_VAL = 0.7225;

    // Wench
    public static final double WENCH_MIN_VAL = 0.2125;
    public static final double WENCH_MAX_VAL = 0.9;

    // AClimber
    public static final double ACLIMBER_MIN_VAL = 0.2575;
    public static final double ACLIMBER_MAX_VAL = 1.0;

    // Jackson Thing
    public static final double JACKSON_THING_MIN_VAL = 0.32;
    public static final double JACKSON_THING_MAX_VAL = 0.55;



    @Override
    public void init() {
        // Map Motors
        BLWheel = hardwareMap.dcMotor.get("BL");
        BRWheel = hardwareMap.dcMotor.get("BR");
        //FLWheel = hardwareMap.dcMotor.get("FL");
        //FRWheel = hardwareMap.dcMotor.get("FR");
        LLift = hardwareMap.dcMotor.get("LL");
        RLift = hardwareMap.dcMotor.get("RL");
        Top = hardwareMap.dcMotor.get("LT");

        // Map Servos
        LFront = hardwareMap.servo.get("LF");
        RFront = hardwareMap.servo.get("RF");
        Intake = hardwareMap.servo.get("I");
        ILift = hardwareMap.servo.get("IL");
        Climber = hardwareMap.servo.get("C");
        HServo = hardwareMap.servo.get("H");
        AClimber = hardwareMap.servo.get("AC");
        Wench = hardwareMap.servo.get("W");
        JacksonThing = hardwareMap.servo.get("JT");

        // Map Sensors
        gyro = hardwareMap.gyroSensor.get("G");
        ultra = hardwareMap.ultrasonicSensor.get("U");
        eopd2 = hardwareMap.ultrasonicSensor.get("E");

        // Motor Reversals
        //RLift.setDirection(DcMotor.Direction.REVERSE);
        BRWheel.setDirection(DcMotor.Direction.REVERSE);
        //FLWheel.setDirection(DcMotor.Direction.REVERSE);
        BLWheel.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void init_loop() {

    }

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
    @Override
    public void loop() {

    }

    /*
     * This makes the thread sleep for the entered amount of time
     * @param milli - This is the amount of time for the thread to sleep in milliseconds
     */
    void doDaSleep(int milli)
    {
        try {
            Thread.sleep(milli);
        } catch (InterruptedException ex) {

        }
    }

    /*
    * This runs the right side of the robot at a specified power
    * @param power - the power to set the wheels at
    */
    void runRightWheels(double power)
    {
        //FRWheel.setPower(power);
        BRWheel.setPower(power);
    }

    /*
     * This runs the right side of the robot at a specified power
     * @param power - the power to set the wheels
     */
    void runLeftWheels(double power)
    {
        BLWheel.setPower(power);
        //FLWheel.setPower(power);
    }

    /*
     * This runs the lift at the power that it is given
     * @param power - the power to run the lift
     */
    void runLift(double power)
    {
        RLift.setPower(power);
        LLift.setPower(power);
    }

    /*
     * This runs the linear slide at the power that it is given
     * @param power - the power to run the linear slide
     */
    void runLinearSlide(double power)
    {
        Top.setPower(power);
    }

} // DE
