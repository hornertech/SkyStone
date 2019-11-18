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

        Robot.moveBackwardForTime(0.5, 325, false);
        Robot.moveLeftForTime(0.6, 2050, false);
        Robot.ClampDown(200);
        sleep(1000);
        Robot.moveRightForTime(1, 1500, false);
        Robot.slowTurn(50);
        Robot.moveRightForTime(1, 1750, false);
        Robot.ClampUp(100);
        Robot.moveRightForTime(0.5, 1000, false);
        Robot.moveForwardForTime(0.5, 1700, false);
        Robot.moveLeftForTime(1, 800, false);
        Robot.moveBackwardForTime(0.5, 980, false);
        Robot.moveForwardForTime(0.2, 150, false);
        Robot.moveRightForTime(0.5, 1200, false);
        Robot.moveLeftForTime(0.5, 50, false);
        Robot.moveForwardForTime(1, 600, false);
    }


}