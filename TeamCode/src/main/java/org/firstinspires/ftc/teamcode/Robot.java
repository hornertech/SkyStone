package org.firstinspires.ftc.teamcode;

import android.util.Log;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;


public class Robot extends java.lang.Thread {

    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private static final int TICKS_PER_ROTATION = 1440; //Tetrix motor specific
    private static final int WHEEL_DIAMETER = 6; //Wheel diameter in inches
    public String TAG = "FTC";

    public DcMotor Motor_FL;
    public DcMotor Motor_FR;
    public DcMotor Motor_BR;
    public DcMotor Motor_BL;

    public DcMotor Slide_L;
    public DcMotor Slide_R;

    public DcMotor Clamp_L;
    public DcMotor Clamp_R;

    public Servo pincher;

    public ElapsedTime mRuntime;

    public boolean isTeleOp = true;
    public boolean DEBUG_DEBUG = false;
    public boolean DEBUG_INFO = false;

    public long movementFactor = 1;
    public double turnFactor = 7.2;

    Robot(HardwareMap map, Telemetry tel) {
        hardwareMap = map;
        telemetry = tel;
        initDevices();
    }

    public void pause(int milliSec) {
        try {
            sleep(milliSec);
        } catch (Exception e) {
        }
    }

    // This function takes input distance in inches and will return Motor ticks needed
    // to travel that distance based on wheel diameter
    public int DistanceToTick(int distance) {
        // Log.i(TAG, "Enter FUNC: DistanceToTick");

        double circumference = WHEEL_DIAMETER * 3.14;
        double num_rotation = distance / circumference;
        int encoder_ticks = (int) (num_rotation * TICKS_PER_ROTATION);

        //       Log.i(TAG,"Rotation Needed : " + num_rotation);
        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Needed : " + encoder_ticks);
            Log.i(TAG, "Exit FUNC: DistanceToTick");
        }

        return (encoder_ticks);
    }
    // This function takes input Angle (in degrees)  and it will return Motor ticks needed
    // to make that Turn2
    public int AngleToTick(double angle) {
        Log.i(TAG, "Enter FUNC: AngleToTick");

        int encoder_ticks = (int) ((java.lang.Math.abs(angle) * TICKS_PER_ROTATION ) / 360);

        Log.i(TAG, "Ticks needed for Angle : " + encoder_ticks);
        Log.i(TAG, "Exit FUNC: AngleToTick");

        return (encoder_ticks);
    }

    /*****************************************************************************/
    /* Section:      Move to specific distance functions                         */
    /*                                                                           */
    /* Purpose:    Used for moving motor specific inches                         */
    /*                                                                           */
    /* Returns:   Nothing                                                        */
    /*                                                                           */
    /* Params:    IN     power         - Speed  (-1 to 1)                        */
    /*            IN     distance      -  in inches                              */
    /*                                                                           */
    /*****************************************************************************/
    // Move forward to specific distance in inches, with power (0 to 1)
    public void moveForwardToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveForwardToPosition Power : " + power + " and distance : " + distance);
        // Reset all encoders
        Motor_FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        Motor_FL.setTargetPosition((-1) * ticks);
        Motor_FR.setTargetPosition(ticks);
        Motor_BR.setTargetPosition(ticks);
        Motor_BL.setTargetPosition((-1) * ticks);

        //Set power of all motors
        Motor_FL.setPower(power);
        Motor_FR.setPower(power);
        Motor_BR.setPower(power);
        Motor_BL.setPower(power);

        //Set Motors to RUN_TO_POSITION
        Motor_FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        //  while ((Motor_BR.isBusy() && Motor_BL.isBusy()) || (Motor_FR.isBusy() && Motor_FL.isBusy())){
        while (Motor_FL.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveForwardToPosition");
        }
    }

    // Move backward to specific distance in inches, with power (0 to 1)
    public void moveBackwardToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveBackwardToPosition Power : " + power + " and distance : " + distance);

        // Reset all encoders
        Motor_FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        Motor_FL.setTargetPosition(ticks);
        Motor_FR.setTargetPosition((-1) * ticks);
        Motor_BR.setTargetPosition((-1) * ticks);
        Motor_BL.setTargetPosition(ticks);

        //Set power of all motors
        Motor_FL.setPower(power * 0.9);
        Motor_FR.setPower(power);
        Motor_BR.setPower(power);
        Motor_BL.setPower(power * 1.1);

        //Set Motors to RUN_TO_POSITION
        Motor_FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        // while ((Motor_FL.isBusy() && Motor_BL.isBusy()) || (Motor_FR.isBusy() && Motor_BR.isBusy())){
        while (Motor_FL.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveBackwardToPosition");
        }
    }

    // Move Left to specific distance in inches, with power (0 to 1)
    public void moveLeftToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveLeftToPosition Power : " + power + " and distance : " + distance);

        // Reset all encoders
        Motor_FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        Motor_FL.setTargetPosition(ticks);
        Motor_FR.setTargetPosition(ticks);
        Motor_BR.setTargetPosition((-1) * ticks);
        Motor_BL.setTargetPosition((-1) * ticks);

        //Set power of all motors
        Motor_FL.setPower(power);
        Motor_FR.setPower(power * 1.03);
        Motor_BR.setPower(power * 1.06);
        Motor_BL.setPower(power * 1.08);

        //Set Motors to RUN_TO_POSITION
        Motor_FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        //Wait for them to reach to the position
        // while ((Motor_FR.isBusy() && Motor_BL.isBusy()) || (Motor_FL.isBusy() && Motor_BR.isBusy())){
        while (Motor_FL.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }

        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveLeftToPosition");
        }
    }

    // Move Right to specific distance in inches, with power (0 to 1)
    public void moveRightToPosition(double power, int distance) {
        Log.i(TAG, "Enter Function: moveRightToPosition Power : " + power + " and distance : " + distance);

        // Reset all encoders
        Motor_FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = DistanceToTick(distance);

        // Set the target position for all motors (in ticks)
        Motor_FL.setTargetPosition((-1) * ticks);
        Motor_FR.setTargetPosition((-1) * ticks);
        Motor_BR.setTargetPosition(ticks);
        Motor_BL.setTargetPosition(ticks);

        //Set power of all motors
        Motor_FL.setPower(power);
        Motor_FR.setPower(power);
        Motor_BR.setPower(power);
        Motor_BL.setPower(power);

        //Set Motors to RUN_TO_POSITION
        Motor_FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        //Wait for them to reach to the position
        // while ((Motor_FL.isBusy() && Motor_BR.isBusy()) || (Motor_FR.isBusy() && Motor_BL.isBusy())){
        while (Motor_FL.isBusy()) {
            if (DEBUG_DEBUG) {
                Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
                Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            }
            //Waiting for Robot to travel the distance
            telemetry.addData("Backward", "Moving");
            telemetry.update();
        }

        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveRightToPosition");
        }
    }
    // Move Right to specific distance in inches, with power (0 to 1)
    public void turnWithAngleAnticlockwise(double power, int angle) {
        Log.i(TAG, "Enter Function: moveRight Power : " + power + " and angle : " + angle);

        int orientation = 1;
        if (angle > 0) {
            Log.i(TAG, "Turning Clockwise");
        } else {
            orientation = -1;
            Log.i(TAG, "Turning Anti-Clockwise");
        }
        try {
            sleep(1000);
        } catch (Exception e) {
        }
        // Reset all encoders
        Motor_FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = AngleToTick(angle);
        ticks = (int) (ticks * 2.85);
        // Set the target position for all motors (in ticks)
        Motor_FL.setTargetPosition(orientation * ticks);
        Motor_FR.setTargetPosition(orientation * ticks);
        Motor_BR.setTargetPosition(orientation * ticks);
        Motor_BL.setTargetPosition(orientation * ticks);

        //Set power of all motors
        Motor_FL.setPower(power);
        Motor_FR.setPower(power);
        Motor_BR.setPower(power);
        Motor_BL.setPower(power);

        //Set Motors to RUN_TO_POSITION
        Motor_FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        while (Motor_FL.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Turning", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        Log.i(TAG, "TICKS needed : " + ticks);
        Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
        Log.i(TAG, "Exit Function: turnNew");
    }

    // Move Right to specific distance in inches, with power (0 to 1)
    public void turnWithAngleClockwise (double power, int angle) {
        Log.i(TAG, "Enter Function: moveRight Power : " + power + " and angle : " + angle);

        int orientation = 1;
        if (angle > 0) {
            Log.i(TAG, "Turning Clockwise");
        } else {
            orientation = -1;
            Log.i(TAG, "Turning Anti-Clockwise");
        }
        try {
            sleep(1000);
        } catch (Exception e) {
        }
        // Reset all encoders
        Motor_FL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = AngleToTick(angle);
        ticks = (int) (ticks * 3.5)* (-1);
        // Set the target position for all motors (in ticks)
        Motor_FL.setTargetPosition(orientation * ticks);
        Motor_FR.setTargetPosition(orientation * ticks);
        Motor_BR.setTargetPosition(orientation * ticks);
        Motor_BL.setTargetPosition(orientation * ticks);

        //Set power of all motors
        Motor_FL.setPower(power);
        Motor_FR.setPower(power);
        Motor_BR.setPower(power);
        Motor_BL.setPower(power);

        //Set Motors to RUN_TO_POSITION
        Motor_FL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_FR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor_BL.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        while (Motor_FL.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Turning", "Moving");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        Log.i(TAG, "TICKS needed : " + ticks);
        Log.i(TAG, "Actual Ticks Motor0 : " + Motor_FL.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor1 : " + Motor_FR.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor2 : " + Motor_BR.getCurrentPosition());
        Log.i(TAG, "Actual Ticks Motor3 : " + Motor_BL.getCurrentPosition());
        Log.i(TAG, "Exit Function: turnNew");
    }
    /*****************************************************************************/
    /* Section:      Move For specific time functions                            */
    /*                                                                           */
    /* Purpose:    Used if constant speed is needed                              */
    /*                                                                           */
    /* Returns:   Nothing                                                        */
    /*                                                                           */
    /* Params:    IN     power         - Speed  (-1 to 1)                        */
    /*            IN     time          - Time in MilliSeconds                    */
    /*                                                                           */
    /*****************************************************************************/
    // Move forward for specific time in milliseconds, with power (0 to 1)
    public void moveForwardForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveForwardForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = Motor_FL.getCurrentPosition();
        long motor1_start_position = Motor_FR.getCurrentPosition();
        long motor2_start_position = Motor_BR.getCurrentPosition();
        long motor3_start_position = Motor_BL.getCurrentPosition();

        if (speed == true) {
            Motor_FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            Motor_FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //Set power of all motors
        Motor_FL.setPower((-1) * power);
        Motor_FR.setPower(power);
        Motor_BR.setPower(power);
        Motor_BL.setPower((-1) * power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        long motor0_end_position = Motor_FL.getCurrentPosition();
        long motor1_end_position = Motor_FR.getCurrentPosition();
        long motor2_end_position = Motor_BR.getCurrentPosition();
        long motor3_end_position = Motor_BL.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));

        }
        Log.i(TAG, "Exit Function: moveForwardForTime");
    }

    public void moveBackwardForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveBackwardForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = Motor_FL.getCurrentPosition();
        long motor1_start_position = Motor_FR.getCurrentPosition();
        long motor2_start_position = Motor_BR.getCurrentPosition();
        long motor3_start_position = Motor_BL.getCurrentPosition();

        if (speed == true) {
            Motor_FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        } else {
            Motor_FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

        //Set power of all motors
        Motor_FL.setPower(power);
        Motor_FR.setPower((-1) * power);
        Motor_BR.setPower((-1) * power);
        Motor_BL.setPower(power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        long motor0_end_position = Motor_FL.getCurrentPosition();
        long motor1_end_position = Motor_FR.getCurrentPosition();
        long motor2_end_position = Motor_BR.getCurrentPosition();
        long motor3_end_position = Motor_BL.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));

        }
        Log.i(TAG, "Exit Function: moveBackwardForTime");
    }

    public void moveRightForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveRightForTime Power : " + power + " and time : " + time + "Speed : " + speed);
        // Reset all encoders
        long motor0_start_position = Motor_FL.getCurrentPosition();
        long motor1_start_position = Motor_FR.getCurrentPosition();
        long motor2_start_position = Motor_BR.getCurrentPosition();
        long motor3_start_position = Motor_BL.getCurrentPosition();

        if (speed == true) {
            Motor_FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //Set power of all motors
            Motor_FL.setPower((-1) * power);
            Motor_FR.setPower((-1) * power);
            Motor_BR.setPower(power);
            Motor_BL.setPower(power);
        } else {
            Motor_FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            //Set power of all motors
            Motor_FL.setPower((-1) * power);
            Motor_FR.setPower((-1) * power);
            Motor_BR.setPower(power);
            Motor_BL.setPower(power);
        }

        try {
            sleep(time);
        } catch (Exception e) {
        }
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        long motor0_end_position = Motor_FL.getCurrentPosition();
        long motor1_end_position = Motor_FR.getCurrentPosition();
        long motor2_end_position = Motor_BR.getCurrentPosition();
        long motor3_end_position = Motor_BL.getCurrentPosition();

        if (DEBUG_INFO) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));

        }
        Log.i(TAG, "Exit Function: moveRightForTime");
    }

    public void moveLeftForTime(double power, int time, boolean speed) {
        Log.i(TAG, "Enter Function: moveLeftForTime Power : " + power + " and time : " + time + "Speed : " + speed);

        long motor0_start_position = Motor_FL.getCurrentPosition();
        long motor1_start_position = Motor_FR.getCurrentPosition();
        long motor2_start_position = Motor_BR.getCurrentPosition();
        long motor3_start_position = Motor_BL.getCurrentPosition();

        if (speed == true) {
            Motor_FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //Set power of all motors
            Motor_FL.setPower(power);
            Motor_FR.setPower(power);
            Motor_BR.setPower((-1) * power);
            Motor_BL.setPower((-1) * power);
        } else {
            Motor_FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            //Set power of all motors
            Motor_FL.setPower(power);
            Motor_FR.setPower(power );
            Motor_BR.setPower((-1) * power);
            Motor_BL.setPower((-1) * power);
        }

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);

        long motor0_end_position = Motor_FL.getCurrentPosition();
        long motor1_end_position = Motor_FR.getCurrentPosition();
        long motor2_end_position = Motor_BR.getCurrentPosition();
        long motor3_end_position = Motor_BL.getCurrentPosition();

        if (DEBUG_INFO ) {
            Log.i(TAG, "Ticks Moved Motor0 : " + (motor0_end_position - motor0_start_position));
            Log.i(TAG, "Ticks Moved Motor1 : " + (motor1_end_position - motor1_start_position));
            Log.i(TAG, "Ticks Moved Motor2 : " + (motor2_end_position - motor2_start_position));
            Log.i(TAG, "Ticks Moved Motor3 : " + (motor3_end_position - motor3_start_position));

        }
        Log.i(TAG, "Exit Function: moveLeftForTime");
    }

     public void turnForTime(double power, int time, boolean speed, int orientation) {
        Log.i(TAG, "Enter Function: turnForTime Power : " + power + " and time : " + time + "Speed : " + speed + "orientation : " + orientation);

        if (speed == true) {
            Motor_FL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //Set power of all motors
            Motor_FL.setPower(orientation * power);
            Motor_FR.setPower(orientation * power);
            Motor_BR.setPower(orientation * power);
            Motor_BL.setPower(orientation * power);
        } else {
            Motor_FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Motor_BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            //Set power of all motors
            //Motor_FL.setPower(orientation * power * 1.6);
            Motor_FL.setPower(orientation * power);
            Motor_FR.setPower(orientation * power);
            Motor_BR.setPower(orientation * power);
            //Motor_BL.setPower(orientation * power * 1.5);
            Motor_BL.setPower(orientation * power);
        }

      /*  Motor_FL.setPower(orientation * power * 0.75);
        Motor_FR.setPower(orientation * power * 1.5);
        Motor_BR.setPower(orientation * power * 1.5);
        Motor_BL.setPower(orientation * power *0.75); */
        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);
    }



    public void moveB(double power, long distance) {
        Motor_FL.setPower(power);
        Motor_FR.setPower((-1) * power);
        Motor_BR.setPower((-1) * power);
        Motor_BL.setPower(power);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveF(double power, long distance) {
        Motor_FL.setPower((-1) * power); //FL
        Motor_FR.setPower(power); //FR
        Motor_BR.setPower(power); //BR
        Motor_BL.setPower((-1) * power); //BL
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);
        telemetry.addData("Direction", "Backward");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveR(double power, long distance) {

        Motor_FL.setPower(power );
        Motor_FR.setPower(power);
        Motor_BR.setPower((-1) * power);
        Motor_BL.setPower((-1) * power);
        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);
        telemetry.addData("Direction", "Right");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void moveL(double power, long distance) {

        Motor_FL.setPower((-1) * power);
        Motor_FR.setPower((-1) * power);
        Motor_BR.setPower(power );
        Motor_BL.setPower(power);

        try {
            sleep(distance * movementFactor);
        } catch (Exception e) {
        }
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);
        telemetry.addData("Direction", "Left");
        telemetry.update();
        if (isTeleOp == false) pause(250);
    }

    public void ClampDown(int time){
        Clamp_L.setPower(0.4);
        Clamp_R.setPower(-0.4);
        try {
            sleep(time);
        } catch (Exception e) {
        }
        Clamp_L.setPower(0);
        Clamp_R.setPower(0);
    }
    public void ClampUp(int time){
        Clamp_L.setPower(-0.4);
        Clamp_R.setPower(0.4);
        try {
            sleep(time);
        } catch (Exception e) {
        }
        Clamp_L.setPower(0);
        Clamp_R.setPower(0);
    }
    public void slowTurn(double angle) {
        Log.i(TAG, "Enter Function slowTurn Angle: "+ angle);

        Motor_FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (angle > 0) {
            Motor_FL.setPower(0.5);
            Motor_FR.setPower(0.5);
            Motor_BR.setPower(0.5);
            Motor_BL.setPower(0.5);
            try {
                sleep((int)(angle * turnFactor));
            } catch (Exception e) {
            }
            Motor_FL.setPower(0);
            Motor_FR.setPower(0);
            Motor_BR.setPower(0);
            Motor_BL.setPower(0);
            if (isTeleOp == false) pause(250);
            if (isTeleOp == false) pause(250);
        } else {
            Motor_FL.setPower(-0.5);
            Motor_FR.setPower(-0.5);
            Motor_BR.setPower(-0.5);
            Motor_BL.setPower(-0.5);
            try {
                sleep((int)(-1 * angle * turnFactor));
            } catch (Exception e) {
            }
            Motor_FL.setPower(0);
            Motor_FR.setPower(0);
            Motor_BR.setPower(0);
            Motor_BL.setPower(0);
            if (isTeleOp == false) pause(250);
            if (isTeleOp == false) pause(250);
        }
        Log.i(TAG, "Exit Function slowTurn");
    }

    //CLEANUP: remove this and use moveLeft/RightForTime
    public void wall_align(double power, int time) {
        Motor_FL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Motor_FR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Motor_BR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Motor_BL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        Motor_FL.setPower(-1 * power);
        Motor_FR.setPower(-1 * power);
        Motor_BR.setPower(power);
        Motor_BL.setPower(power);
        try {
            sleep(time);
        } catch (Exception e) {
        }
        Motor_FL.setPower(0);
        Motor_FR.setPower(0);
        Motor_BR.setPower(0);
        Motor_BL.setPower(0);
    }

    public void moveSlideUp(double power, double rotation)
    {
        Log.i(TAG, "Enter Function moveSlideUp Power: "+ power + " Rotation :" + rotation);
        // Reset all encoders
        Slide_R.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Slide_L.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = (int) (rotation * TICKS_PER_ROTATION);

        // Set the target position for all motors (in ticks)
        Slide_L.setTargetPosition(ticks);
        Slide_R.setTargetPosition((-1) * ticks);


        //Set power of all motors
        Slide_R.setPower(power);
        Slide_L.setPower(power);

        //Set Motors to RUN_TO_POSITION
        Slide_R.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Slide_L.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        // while ((Motor_FL.isBusy() && Motor_BL.isBusy()) || (Motor_FR.isBusy() && Motor_BR.isBusy())){
        while (Slide_R.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Slide", "UP");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        Slide_R.setPower(0);
        Slide_L.setPower(0);


        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + Slide_R.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + Slide_L.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveSlideUp");
        }
        Log.i(TAG, "Exit Function moveSlideUp ");
    }

    public void moveSlideDown(double power, double rotation)
    {
        Log.i(TAG, "Enter Function moveSlideDown Power: "+ power + " Rotation :" + rotation);
        // Reset all encoders
        Slide_R.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Slide_L.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Find the motor ticks needed to travel the required distance
        int ticks = (int) (rotation * TICKS_PER_ROTATION);

        // Set the target position for all motors (in ticks)
        Slide_L.setTargetPosition((-1) * ticks);
        Slide_R.setTargetPosition(ticks);


        //Set power of all motors
        Slide_R.setPower(power);
        Slide_L.setPower(power);

        //Set Motors to RUN_TO_POSITION
        Slide_R.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Slide_L.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        //Wait for them to reach to the position
        // while ((Motor_FL.isBusy() && Motor_BL.isBusy()) || (Motor_FR.isBusy() && Motor_BR.isBusy())){
        while (Slide_R.isBusy()) {
            //Waiting for Robot to travel the distance
            telemetry.addData("Slide", "Down");
            telemetry.update();
        }


        //Reached the distance, so stop the motors
        Slide_R.setPower(0);
        Slide_L.setPower(0);


        if (DEBUG_INFO) {
            Log.i(TAG, "TICKS needed : " + ticks);
            Log.i(TAG, "Actual Ticks Motor0 : " + Slide_R.getCurrentPosition());
            Log.i(TAG, "Actual Ticks Motor1 : " + Slide_L.getCurrentPosition());
            Log.i(TAG, "Exit Function: moveSlideDown");
        }
        Log.i(TAG, "Exit Function moveSlideDown ");
    }

    public void grabStone() {
        Log.i(TAG, "Enter Function grabStone Start Position:" + pincher.getPosition());
        pincher.setPosition(0.72);
        Log.i(TAG, "Exit Function grabStone End Position:" + pincher.getPosition());
    }

    public void dropStone() {
        Log.i(TAG, "Enter Function dropStone Start Position:" + pincher.getPosition());
        pincher.setPosition(0.84);
        Log.i(TAG, "Exit Function dropStone Start Position:" + pincher.getPosition());
     }

    public void setPosition(double position) {
        Log.i(TAG, "Enter Function setPosition Start Position:" + pincher.getPosition());
       pincher.setPosition(position);
        Log.i(TAG, "Exit Function setPosition End Position:" + pincher.getPosition());
    }

    private void initDeviceCore() throws Exception {

        telemetry.addData("Please wait", "In function init devices");
        telemetry.update();

        //Wheels
        Motor_FL = hardwareMap.get(DcMotor.class, "motor_br");
        Motor_FR = hardwareMap.get(DcMotor.class, "motor_bl");
        Motor_BR = hardwareMap.get(DcMotor.class, "motor_fr");
        Motor_BL = hardwareMap.get(DcMotor.class, "motor_fl");

        Slide_R = hardwareMap.get(DcMotor.class, "slide_r");
        Slide_L = hardwareMap.get(DcMotor.class, "slide_l");

        Clamp_L = hardwareMap.get(DcMotor.class, "clamp_l");
        Clamp_R = hardwareMap.get(DcMotor.class, "clamp_r");

        pincher = hardwareMap.get(Servo.class, "pincher");

        telemetry.addData("Status", "Initialized");
        telemetry.update();


    }


    private void initDevices() {
        mRuntime = new ElapsedTime();
        mRuntime.reset();

        try {
            initDeviceCore();
        } catch (Exception e) {
            telemetry.addData("Exception", "In function init devices" + e);
            telemetry.update();
            try {
                sleep(10000);
            } catch (Exception e1) {
            }

        }

    }
    public void moveSlides(double power, int time) {
        // Reset all encoders
        long slide_R_Start = Slide_R.getCurrentPosition();
        long slide_L_Start = Slide_L.getCurrentPosition();

            Slide_L.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            Slide_R.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //Set power of all motors
        Slide_L.setPower((-1) * power);
        Slide_R.setPower(power);

        try {
            sleep(time);
        } catch (Exception e) {
        }

        //Reached the distance, so stop the motors
        Slide_L.setPower(0);
        Slide_R.setPower(0);

        long Slide_R_End = Slide_R.getCurrentPosition();
        long Slide_L_End = Slide_L.getCurrentPosition();
        Log.i(TAG, "Exit Function: moveForwardForTime");
    }



}
