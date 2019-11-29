package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;



import org.firstinspires.ftc.robotcore.external.ClassFactory;


@Autonomous
public class buildingRed extends LinearOpMode {

    public String TAG = "FTC";
    //---------------------------------------------------------------------------------------

    public void runOpMode() {

        org.firstinspires.ftc.teamcode.Robot Robot = new org.firstinspires.ftc.teamcode.Robot(hardwareMap, telemetry);

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();
        Log.i(TAG, "*************Starting Autonomous TEST**************************");

        Robot.moveForwardForTime(0.5, 400, false);
        Robot.moveLeftForTime(0.6, 500, false);
        sleep(400);
        Robot.fixOrientation(0);
        Robot.moveLeftForTime(0.6, 1600, false);
        Robot.ClampDown(200);
        sleep(1000);
        Robot.moveRightForTime(1, 2250, false);
        sleep(300);
        Robot.fixOrientation(10);
        //sleep(300);
        //Robot.fixOrientation(0);
        //Robot.slowTurn(-20);
        Robot.moveRightForTime(1, 1900, false);
        Robot.ClampUp(150);
        Robot.moveRightForTime(0.3, 1500, false);  // allign with alliance wall
        //Robot.moveBackwardForTime(0.3, 2000, false);
        Robot.moveBackwardForTime(0.7, 1000, false);
        Robot.moveLeftForTime(1, 800, false);
        Robot.slowTurn(180);
        sleep(400);
        Robot.fixOrientation(180);
        Robot.moveBackwardForTime(0.3, 1750, false);
        Robot.moveForwardForTime(0.2, 150, false);
        Robot.moveLeftForTime(0.5, 1200, false);
        Robot.moveRightForTime(0.5, 50, false);
        Robot.moveForwardForTime(1, 700, false);
        Robot.moveLeftForTime(0.25, 2000, false);
    }


}