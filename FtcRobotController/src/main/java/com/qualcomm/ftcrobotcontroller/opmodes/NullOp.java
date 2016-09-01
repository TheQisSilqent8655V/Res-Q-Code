/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TeleOp Mode
 * <p>
 *Enables control of the robot via the gamepad
 */
public class NullOp extends OpMode {

    int davalue = 0;
    public static final int RED = 1;
    public static final int BLUE = 2;
    public static final int FORWARD = -1;
    public static final int BACKWARD = 1;
    public static final boolean CLOSE = true;
    public static final boolean FAR = false;

    double alliance = RED;
    int delay = 0;
    boolean defense = true;
    boolean startpos = CLOSE;

  @Override
  public void init() {

  }

  /*
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */
  @Override
  public void init_loop() {
      if(gamepad1.a)
      {
          if (alliance == RED) {
              alliance = BLUE;
          }
          else {
              alliance = RED;
          }

          while(gamepad1.a)
          {

          }
      }

      if(gamepad1.b)
      {
          if(startpos == CLOSE) {
              startpos = FAR;
          }
          else {
              startpos = CLOSE;
          }

          while(gamepad1.b)
          {

          }
      }

      if(gamepad1.x)
      {
          if(defense == true) {
              defense = false;
          }
          else {
              defense = true;
          }
          while(gamepad1.x)
          {

          }
      }
      telemetry.addData("Defense - ", defense);

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

  }
}
