package org.firstinspires.ftc.teamcode;


import android.util.Log;

import com.qualcomm.hardware.bosch.BNO055IMU;
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

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;


import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;


@Autonomous
public class loadingBlue extends LinearOpMode {

    DigitalChannel digitalTouch;  // Hardware Device Object
    //---------------------------------------------------------------------------------------

    public String TAG = "FTC";
    //---------------------------------------------------------------------------------------



    private static final String VUFORIA_KEY = "AV8zEej/////AAABmVo2vNWmMUkDlkTs5x1GOThRP0cGar67mBpbcCIIz/YtoOvVynNRmJv/0f9Jhr9zYd+f6FtI0tYHqag2teC5GXiKrNM/Jl7FNyNGCvO9zVIrblYF7genK1FVH3X6/kQUrs0vnzd89M0uSAljx0mAcgMEEUiNOUHh2Fd7IOgjlnh9FiB+cJ8bu/3WeKDxnDdqx6JI5BlQ4w7YW+3X2icSRDRlvE4hhuW1VM1BTPQgds7OtHKqUn4Z5w1Wqg/dWiOHxYTww28PVeg3ae4c2l8FUtE65jr2qQdQNc+DMLDgnJ0fUi9Ww28OK/aNrQQnHU97TnUgjLgCTlV7RXpfut5mZWXbWvO6wA6GGkm3fAIQ2IPL";
    private VuforiaLocalizer vuforia = null;

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

    private double location[] = {0, 0, 0, 0, 0, 0, 0};
    // Location[0] : 0 = Target not visible, 1 = Target visible
    // Location[1] : Lateral drift from target
    // Location[2] : Distance from Target
    // Location[3] : Vertical shift, not needed for our program
    // Location[4]
    private int boardDistance = 30;
    private int bridgeOffset = 10;
    private int skystonePicked = 0;
    private int skystoneLocation = 0;
    private int stoneStrafeTime = 700;
    private int stoneForwardTime = 200;
    private double correctionDistance = 0;
    void detectOnce(List<VuforiaTrackable> allTrackables) {
        // check all the trackable targets to see which one (if any) is visible. -- Our only Trackable is Skystone
        Log.i(TAG, "Entering Function detectOnce");
        targetVisible = false;
        location[0] = 0;
        for (VuforiaTrackable trackable : allTrackables) {
            if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
                telemetry.addData("Visible Target", trackable.getName());
                targetVisible = true;

                // getUpdatedRobotLocation() will return null if no new information is available since
                // the last time that call was made, or if the trackable is not currently visible.
                OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
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
            location[0] = 1;
            telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
                    translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);

            location[1] = translation.get(0) / mmPerInch;
            location[2] = translation.get(1) / mmPerInch;
            location[3] = translation.get(2) / mmPerInch;
            // express the rotation of the robot in degrees.
            Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
            telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);

            location[4] = rotation.firstAngle;
            location[5] = rotation.secondAngle;
            location[6] = rotation.thirdAngle;
            for(int check = 1; check < 7; check++){
                if(location[check] > 30){
                    location[0] = 0;
                    break;
                }
            }
            Log.i(TAG, "Positions: X =" + location[1] + " Y = " + location[2] + " Z = " + location[3]);
            Log.i(TAG, "Angles: ROLL =" + location[4] + "Pitch = " + location[5] + "Heading = " + location[6]);
        } else {
            telemetry.addData("Visible Target", "none");
            Log.i(TAG, "No Target Visible");
        }
        telemetry.update();
    }


    public void moveToSkyStone(org.firstinspires.ftc.teamcode.Robot robot) {
        if (java.lang.Math.abs(location[6]) > 1){
            robot.slowTurn((int)location[6] * (-1));
        }
        if (location[2] > 0)
        {
            correctionDistance = (location[2]-2.5)*1.35;
            robot.moveRightToPosition(0.5, (int) correctionDistance);
            if (location[2] >= 5 ){
                skystoneLocation++;
            }
        }
        else if (location[2] < 0)
        {
            correctionDistance = (location[2])*1.6;
            robot.moveLeftToPosition(0.5, java.lang.Math.abs((int) correctionDistance));
            if (location[2] <= -5 ){
                skystoneLocation--;
            }
        }

        robot.moveForwardToPosition(0.6, (java.lang.Math.abs((int) location[1]) - 3));
    }


    //The Autonomous Program
    public void runOpMode() {


        int detect_result;
        int i;


        // initVuforia();


        //Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CAMERA_CHOICE;

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
        final float CAMERA_FORWARD_DISPLACEMENT = 0.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot center
        final float CAMERA_VERTICAL_DISPLACEMENT = 5.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
        final float CAMERA_LEFT_DISPLACEMENT = 0;     // eg: Camera is ON the robot's center line

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
            phoneXRotate = 90;
        }
        OpenGLMatrix robotFromCamera = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
                .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));

        //confirm position of phone
        for (VuforiaTrackable trackable : allTrackables) {
            ((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, parameters.cameraDirection);
        }

        org.firstinspires.ftc.teamcode.Robot Robot = new org.firstinspires.ftc.teamcode.Robot(hardwareMap, telemetry);


        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();
        Log.i(TAG, "*************Starting Autonomous TEST**************************");

        targetsSkyStone.activate();

        Robot.grabStone1();

        Robot.moveWithSlide1(0.4, 750, 1, 1.8, 1);
        //  while (!isStopRequested()) {
        Robot.dropStone1();
        Robot.dropStone1();
        for (i = skystoneLocation; i < 6; i++) {
            sleep(300);
            detectOnce(allTrackables);
            detectOnce(allTrackables);
            if (location[0] == 1) {
                skystoneLocation = i;
                skystonePicked++;
                Robot.moveSlideDown(1, 1.8);
                moveToSkyStone(Robot);
                Log.i(TAG, "Detected Stone at Location : " + skystoneLocation + 1);
                Robot.grabStone1();
                sleep(200);
                Robot.moveBackwardForTime(1, 125, false); // move little back
                Robot.slowTurn(90);
                sleep(300);
                Robot.fixOrientation(90);
                Robot.moveForwardForTime(1, 810 + (skystoneLocation + 1) * 275, false);
                Robot.moveWithSlide1(0.16, 1200, 1, 2.2, 1);
                Robot.dropStone1();
                Robot.moveWithSlide(1, 10, -1, 1.95, -1);
                if (skystonePicked == 2) {
                    Robot.moveRightForTime(1, 50, false);
                    Robot.moveBackwardForTime(1, 300, false);
                    skystoneLocation = 6;
                    break;
                } else {
                    // go to detect second skystone
                    Robot.moveBackwardForTime(1, 900 + ((skystoneLocation + 1) * stoneForwardTime), false);
                    Robot.moveSlideUp(1, 1.8);
                    Robot.slowTurn(-90);
                    sleep(300);
                    Robot.fixOrientation(0);
                }

            } else {
                Robot.slowTurn(-0.5);
                Robot.moveRightForTime(0.5, stoneStrafeTime, false);
            }
            if (i == 5 & skystonePicked != 2) {
                Robot.moveSlideDown(1, 1.8);
                Robot.moveRightForTime(0.5, 600, false);
                Robot.moveLeftForTime(0.25, 330, false);
                if (skystonePicked == 0) {
                    Robot.moveForwardForTime(1, 300, false);
                } else {
                    Robot.moveForwardForTime(1, 150, false);
                }
                Robot.slowTurn(-20);
                Robot.moveForwardForTime(0.4, 290, false);
                Robot.grabStone1();
                sleep(500);
                Robot.moveBackwardForTime(1, 200, false);
                Robot.slowTurn(112.5);
                Robot.moveForwardForTime(1, 2390, false);
                Robot.dropStone1();
                Robot.moveBackwardForTime(1, 450, false);
            }
        }

        targetsSkyStone.deactivate();

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

}