package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
@Disabled
public class buildingRed extends LinearOpMode {

    public String TAG = "FTC";
    //---------------------------------------------------------------------------------------

    public void runOpMode() {

        org.firstinspires.ftc.teamcode.Robot Robot = new org.firstinspires.ftc.teamcode.Robot(hardwareMap, telemetry);

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();
        Log.i(TAG, "*************Starting Autonomous TEST**************************");


//BEGINNING AUTONOMOUS CODE
        /*
        Our autonomous code for the Building Side Red is quick and simple
            1) First, we move left to get our clamping mehcanism right up next to the foundation
            2) We then clamp down and move back to the wall
                - From here we right align with the side wall
                - We then front align with the back wall to be sure of our relative position
            3) To make sure that the foundation is in the area, we go in front of the foundation and
               drive backwards. This makes sure that the wall is right up with the wall
            4) To end, we go park on the middle line
            4) In total, our team can score 15 points from this Loading zone program
         */


        Robot.moveForwardForTime(0.5, 400, false);
        Robot.moveLeftForTime(0.6, 500, false);
        sleep(400);
        Robot.fixOrientation(0);
        Robot.moveLeftForTime(0.6, 1600, false);
        // Get right up next to the Foundation
        Robot.ClampDown(200);
        sleep(1000);
        Robot.moveRightForTime(1, 2250, false);
        sleep(300);
        Robot.fixOrientation(10);
        Robot.moveRightForTime(1, 1900, false);
        // Pull foundation in to the side wall
        Robot.ClampUp(150);
        Robot.moveRightForTime(0.3, 1500, false);
        // Align with side wall
        Robot.moveBackwardForTime(0.7, 1000, false);
        Robot.moveLeftForTime(1, 800, false);
        Robot.slowTurn(180);
        // Get in front of Foundation
        sleep(400);
        Robot.fixOrientation(180);
        Robot.moveBackwardForTime(0.3, 1750, false);
        Robot.moveForwardForTime(0.2, 150, false);
        Robot.moveLeftForTime(0.5, 1200, false);
        Robot.moveRightForTime(0.5, 50, false);
        Robot.moveForwardForTime(1, 700, false);
        // Park
        Robot.moveLeftForTime(0.25, 2000, false);
    }
}