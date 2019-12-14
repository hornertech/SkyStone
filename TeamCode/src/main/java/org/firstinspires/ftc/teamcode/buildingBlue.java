package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;



import org.firstinspires.ftc.robotcore.external.ClassFactory;


@Autonomous
public class buildingBlue extends LinearOpMode {

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
        Our autonomous code for the Building Side Blue is quick and simple
            1) First, we move left to get our clamping mehcanism right up next to the foundation
            2) We then clamp down and move back to the wall
                - From here we right align with the side wall
                - We then front align with the back wall to be sure of our relative position
            3) To make sure that the foundation is in the area, we go in front of the foundation and
               drive backwards. This makes sure that the wall is right up with the wall
            4) To end, we go park on the middle line
            4) In total, our team can score 15 points from this Loading zone program
         */


        Robot.moveBackwardForTime(0.5, 385, false);
        Robot.moveLeftForTime(0.6, 2100, false);
       // sleep(500);
        //Robot.fixOrientation(-1);
       // Robot.moveLeftForTime(0.6, 1600, false);
        // Get right up next to the Foundation
        Robot.ClampDown(200);
        sleep(1000);
        Robot.moveRightForTime(1, 1000, false);
        //sleep(300);
        //Robot.fixOrientation(10);
        Robot.slowTurn(-50);
        Robot.moveRightForTime(1, 1000, false);
        Robot.slowTurn(-50);
        Robot.moveRightForTime(1, 1000, false);
        Robot.slowTurn(-50);
        Robot.moveRightForTime(1, 1100, false);
        // Pull foundation in to the side wall
        Robot.ClampUp(120);
        Robot.moveRightForTime(0.3, 3000, false);
        // Align with side wall
        Robot.moveBackwardForTime(0.3, 2000, false);
        Robot.moveForwardForTime(0.7, 2000, false);
        Robot.moveLeftForTime(1, 800, false);
        // Get in front of Foundation
        Robot.moveBackwardForTime(0.3, 1900, false);
        Robot.moveForwardForTime(0.2, 150, false);
        Robot.moveRightForTime(0.5, 1200, false);
        Robot.moveLeftForTime(0.5, 50, false);
        Robot.moveForwardForTime(1, 700, false);
        // Park
        Robot.moveRightForTime(0.25, 2000, false);
    }
}