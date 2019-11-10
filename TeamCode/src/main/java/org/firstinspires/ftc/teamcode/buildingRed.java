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

        Robot.moveLeftForTime(1, 300, false);
        Robot.ClampDown(100);
        Robot.moveRightForTime(1, 300, false);
        Robot.moveRightForTime(0.2, 2000, false);
        Robot.moveForwardForTime(0.5, 200, false);
        Robot.moveLeftForTime(1, 100, false);
        Robot.moveBackwardForTime(0.5, 100, false);
        Robot.moveRightForTime(0.5, 200, false);
        Robot.moveForwardForTime(1, 200, false);

    }


}