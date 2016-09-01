package com.qualcomm.ftcrobotcontroller.opmodes;

/**
 * Created by Conno on 3/11/2016.
 */
public class RedAutonomousClose extends AllAutonomousFile {

    /*
     * This is the main autonomous thread put autonomous functions in here to
     * run during autonomous
     * Still need to test using joysticks in init loop so we can have only 1
     * autonomous program and switch variables before the match
     */
    Runnable AutoPro = new Runnable() {
        public void run() {
            climbersAuton(0, RED, true, CLOSE, false);
        }
    };

    Thread Autonomous = new Thread(AutoPro);

    /*
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     */
    @Override
    public void loop() {
        if(davalue == 0)
        {
            DaGyroThread.start();
            Autonomous.start();
            davalue++;
        }
        runAllMotorsSlashServos();
        telemetry.addData("gyro - ", darealgyroheading);
        telemetry.addData("Start Ultra - ", startultra);
        telemetry.addData("Stop Ultra - ", ultrasonicreading);
    }
}
