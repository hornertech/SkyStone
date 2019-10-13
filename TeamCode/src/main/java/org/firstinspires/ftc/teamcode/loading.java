package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;


import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;


import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;


@Autonomous
public class loading extends LinearOpMode {

    DigitalChannel digitalTouch;  // Hardware Device Object
    //---------------------------------------------------------------------------------------
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private static final int GOLD_MINERAL_FOUND = 0;
    private static final int SILVER_MINERAL_FOUND = 1;
    private static final int NO_MINERAL_FOUND = 2;
    public boolean debugOn = false;
    public boolean test = false;
    public boolean sensortouch = true;

    public String TAG = "SKYSTONE";
    //---------------------------------------------------------------------------------------

    private static final String VUFORIA_KEY = "AV8zEej/////AAABmVo2vNWmMUkDlkTs5x1GOThRP0cGar67mBpbcCIIz/YtoOvVynNRmJv/0f9Jhr9zYd+f6FtI0tYHqag2teC5GXiKrNM/Jl7FNyNGCvO9zVIrblYF7genK1FVH3X6/kQUrs0vnzd89M0uSAljx0mAcgMEEUiNOUHh2Fd7IOgjlnh9FiB+cJ8bu/3WeKDxnDdqx6JI5BlQ4w7YW+3X2icSRDRlvE4hhuW1VM1BTPQgds7OtHKqUn4Z5w1Wqg/dWiOHxYTww28PVeg3ae4c2l8FUtE65jr2qQdQNc+DMLDgnJ0fUi9Ww28OK/aNrQQnHU97TnUgjLgCTlV7RXpfut5mZWXbWvO6wA6GGkm3fAIQ2IPL";
    private VuforiaLocalizer vuforia = null;
    private TFObjectDetector tfod;

    //Define Position of Camera
    // CAMERA_CHOICE = BACK (rear camera) or FRONT (selfie camera)
    // PHONE_IS_PORTRAIT = true(portrait) or false(landscape)

    private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
    private static final boolean PHONE_IS_PORTRAIT = false  ;


    //Define Measurements
    private static final float mmPerInch        = 25.4f;
    private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor

    // Constant for Stone Target
    private static final float stoneZ = 2.00f * mmPerInch;

    // Class Members
    private OpenGLMatrix lastLocation = null;
    private boolean targetVisible = false;
    private float phoneXRotate    = 0;
    private float phoneYRotate    = 0;
    private float phoneZRotate    = 0;





    //The Autonomous Program
    public void runOpMode() {


        int detect_result;
        initVuforia();
        initTfod();

        //Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection   = CAMERA_CHOICE;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        //Load data sets for trackable objects; data sets found in 'assets'
        VuforiaTrackables targetsSkyStone = this.vuforia.loadTrackablesFromAsset("Skystone");

        //Load stone target object
        VuforiaTrackable stoneTarget = targetsSkyStone.get(0);
        stoneTarget.setName("Stone Target");

        // Set Stone Target Position
        stoneTarget.setLocation(OpenGLMatrix
                .translation(0, 0, stoneZ)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));

        // Next, translate the camera lens to where it is on the robot.
        final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot center
        final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line

        // For convenience, gather together all the trackable objects in one easily-iterable collection */
        List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
        allTrackables.addAll(targetsSkyStone);

        // We need to rotate the camera around its long axis to bring the correct camera forward.
        if (CAMERA_CHOICE == BACK) {
            phoneYRotate = -90;
        } else {
            phoneYRotate = 90;
        }

        // Rotate the phone vertical about the X axis if it's in portrait mode
        if (PHONE_IS_PORTRAIT) {
            phoneXRotate = 90 ;
        }
        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        //confirm position of phone
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }






        //org.firstinspires.ftc.teamcode.Robot robot = new org.firstinspires.ftc.teamcode.Robot(hardwareMap, telemetry);


        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();
        Log.i(TAG, "*************Starting Autonomous TEST**************************");

        targetsSkyStone.activate();

        while (!isStopRequested()) {

            // check all the trackable targets to see which one (if any) is visible. -- Our only Trackable is Skystone
            targetVisible = false;
            for (VuforiaTrackable trackable : allTrackables) {
                if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
                    telemetry.addData("Visible Target", trackable.getName());
                    targetVisible = true;

                    // getUpdatedRobotLocation() will return null if no new information is available since
                    // the last time that call was made, or if the trackable is not currently visible.
                    OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
                    if (robotLocationTransform != null) {
                        lastLocation = robotLocationTransform;
                    }
                    break;
                }
            }

            //I just kept the following as part of the code, but I don't think we need it (-VJ)
            // Provide feedback as to where the robot is located (if we know).
            if (targetVisible) {
                // express position (translation) of robot in inches.
                VectorF translation = lastLocation.getTranslation();
                telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                        translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

                // express the rotation of the robot in degrees.
                Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
                telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
            }
            else {
                telemetry.addData("Visible Target", "none");
            }
            telemetry.update();
        }

        targetsSkyStone.deactivate();




//Last year's code:
/*
    long start_time = System.currentTimeMillis();
    long time_taken;
    if (opModeIsActive()) {
        if (tfod != null) {
            tfod.activate();
        }
    }
    //Begin step 1 - Drop from lander
    Log.i(TAG, "STEP 1: Come Down and Unlatch");


    robot.unlatchUsingEncoderPosition(1, 1, 12);        //Unlatching
    robot.moveRightToPosition(1, 4);                    //Un-hook


    robot.moveBackwardForTime(0.25, 350, false);        //Aligning against lander
    robot.moveForwardForTime(1, 275, true);            //Move forward to Central position
    time_taken = System.currentTimeMillis() - start_time;
    Log.i(TAG, "STEP 1: Completed after : " + time_taken + " milli seconds");
    //End step 1


      telemetry.addData("Detection", "Started");
    telemetry.update();
    Log.i(TAG, "STEP 2: Detect and Dislodge ");


    detect_result = detectOnceTime(robot);
    if (detect_result == GOLD_MINERAL_FOUND) {
        Log.i(TAG, "Gold Mineral detected at Center: Knocking off");
        robot.moveForwardForTime(0.5, 550, true);               //Knock off mineral
        robot.moveBackwardForTime(0.5, 400, true);              //Move backward to Central Position
        robot.pause(100);
        robot.turnWithAngleAnticlockwise(0.5, 80);              //Turn Left
        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 2: Completed after : " + time_taken + " Milli Seconds");
        //End step 2


          Log.i(TAG, "STEP 3: Drop Team Marker ");
        //   robot.pause(100);
        robot.moveForwardAndDropSlide(1, 1350, true);
        //Moving back
        //   robot.pause(100);
        robot.turnWithAngleAnticlockwise(0.5, 45);              //Turn left side
        while (sensortouch == false) {                                      //Wall Allignment


            // send the info back to driver station using telemetry function.
            // if the digital channel returns true it's HIGH and the button is unpressed.
            if (digitalTouch.getState() == true) {
                telemetry.addData("Digital Touch", "Is Not Pressed");
                robot.moveRightForTime(0.3, 600, false);
            } else {
                robot.pause(100);
                telemetry.addData("Digital Touch", "Is Pressed");
                robot.moveLeftForTime(0.6, 200, true);
                robot.moveForwardAndDropSlide(1, 650, true);            //MAKE EDIT HERE IF DOES NOT WORK
                sensortouch = true;
            }
            telemetry.update();
        }                                                       //Coming out after aligning
        //Move forward to go and drop teammarker
        // robot.grabberRotatorMoveTime(1, 2200);                  //Bring grabber down
        robot.turnWithAngleClockwise(0.5, 10);
        robot.releaseMineral(10);                               //Drop Teammarker
        robot.turnWithAngleAnticlockwise(0.5, 10);                            //Drop Teammarker


        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 3: Completed after : " + time_taken + " Milli Seconds");



        Log.i(TAG, "STEP 4: Park At Crater ");
        robot.moveBackwardForTime(1, 1550, true);
        robot.moveBackwardForTime(0.5, 400, true);
        time_taken = System.currentTimeMillis() - start_time;
        Log.i(TAG, "STEP 4: Completed after : " + time_taken + " Milli Seconds");
    } else {//Move right 14.5 in.
        Log.i(TAG, "Silver Mineral Detected at Center: Moving Right");
        robot.turnWithAngleClockwise(0.5, 8);
        robot.moveRightForTime(0.5, 800, true);                 //Move Right to look for Mineral at 2nd location
        detect_result = detectOnceTime(robot);
        if (detect_result == GOLD_MINERAL_FOUND) {
            Log.i(TAG, "Gold Mineral detected at Right location: Knocking off");
            robot.moveForwardForTime(0.5, 475, true);          //Knock off mineral
            //robot.pause(100);
            robot.moveBackwardForTime(0.5, 350, true);         //Move back
            robot.pause(100);
            robot.turnWithAngleAnticlockwise(0.5, 80);         //Turn Left degress
            //  robot.moveForwardForTime(1, 400, true);            //Move back to Central Position
            time_taken = System.currentTimeMillis() - start_time;
            Log.i(TAG, "STEP 2: Completed after : " + time_taken + " Milli Seconds");
            //End step 2



            Log.i(TAG, "STEP 3: Drop Team Marker ");
            //robot.pause(100);
            robot.moveForwardAndDropSlide(1, 1800, true);
            //Moving back
            //robot.pause(100);
            robot.turnWithAngleAnticlockwise(0.8, 45);              //Turn left side
            while (sensortouch == false) {                                      //Wall Allignment


                // send the info back to driver station using telemetry function.
                // if the digital channel returns true it's HIGH and the button is unpressed.
                if (digitalTouch.getState() == true) {
                    telemetry.addData("Digital Touch", "Is Not Pressed");
                    robot.moveRightForTime(0.3, 600, false);
                } else {
                    robot.pause(100);
                    telemetry.addData("Digital Touch", "Is Pressed");
                    robot.moveLeftForTime(0.6, 200, true);
                    robot.moveForwardAndDropSlide(1, 700, true);    //MAKE EDIT HERE IF DOES NOT WORK
                    sensortouch = true;
                }
                telemetry.update();
            }                                                       //Coming out after aligning
            //Move forward to go and drop teammarker
            //robot.grabberRotatorMoveTime(1, 2200);                  //Bring grabber down
            //  robot.turnWithAngleClockwise(0.8, 10);
            robot.releaseMineral(8);                               //Drop Teammarker
            //  robot.turnWithAngleAnticlockwise(0.8, 15);


            time_taken = System.currentTimeMillis() - start_time;
            Log.i(TAG, "STEP 3: Completed after : " + time_taken + " Milli Seconds");


            Log.i(TAG, "STEP 4: Park At Crater ");
            robot.moveBackwardForTime(1, 1600, true);
            time_taken = System.currentTimeMillis() - start_time;
            Log.i(TAG, "STEP 4: Completed after : " + time_taken + " Milli Seconds");
        } else { // Knock of Leftmost Mineral
            Log.i(TAG, "Silver Mineral Detected at Right Location : Knocking of Left Mineral");
            robot.turnWithAngleClockwise(0.5, 10);             //Slight turn Right to align
            robot.moveLeftForTime(0.7, 1450, true);            //Move Left to look for Mineral at 3rd location
            robot.moveForwardForTime(0.5, 575, true);          //Knock off mineral
            robot.moveBackwardForTime(0.5, 425, true);         //Move back
            robot.pause(100);
            robot.turnWithAngleAnticlockwise(0.5, 105);         //Turn Right 90 degress
            //   robot.pause(100);
            //   robot.moveBackwardForTime(.65, 500, true);         //Move backward to Central Position
            time_taken = System.currentTimeMillis() - start_time;
            Log.i(TAG, "STEP 2: Completed after : " + time_taken + " Milli Seconds");
            //End step 2


            Log.i(TAG, "STEP 3: Drop Team Marker ");
            //   robot.pause(100);
            //    robot.moveForwardForTime(1, 1350, true);                //Moving back
            robot.pause(100);
            robot.turnWithAngleAnticlockwise(0.5, 45);              //Turn left side
            while (sensortouch == false) {                                      //Wall Allignment


                // send the info back to driver station using telemetry function.
                // if the digital channel returns true it's HIGH and the button is unpressed.
                if (digitalTouch.getState() == true) {
                    telemetry.addData("Digital Touch", "Is Not Pressed");
                    robot.moveRightForTime(0.3, 600, false);
                } else {
                    robot.pause(100);
                    telemetry.addData("Digital Touch", "Is Pressed");
                    robot.moveLeftForTime(0.6, 200, true);
                    robot.moveForwardAndDropSlide(1, 700, true);        //MAKE EDIT HERE IF DOES NOT WORK
                    sensortouch = true;
                }
                telemetry.update();
            }                                                       //Coming out after aligning & Move forward to go and drop teammarker
            robot.grabberRotatorMoveTime(1, 600);                  //Bring grabber down
            robot.turnWithAngleClockwise(0.8, 10);
            robot.releaseMineral(10);                               //Drop Team Marker
            if (test) {
                robot.turnWithAngleAnticlockwise(0.8, 10);
                time_taken = System.currentTimeMillis() - start_time;
                Log.i(TAG, "STEP 3: Completed after : " + time_taken + " Milli Seconds");


                Log.i(TAG, "STEP 4: Park At Crater ");
                robot.moveBackwardForTime(1, 1650, true);
            }
            time_taken = System.currentTimeMillis() - start_time;
            Log.i(TAG, "STEP 4: Completed after : " + time_taken + " Milli Seconds");
        }
    */


        if (tfod != null) {
            tfod.shutdown();
        }
        Log.i(TAG, "================== Autonomous TEST Finished =======================");
    }



    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();


        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;


        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);


        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }


    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
}