package org.firstinspires.ftc.teamcode;
//Fix if detecting 2 or 0 minerals
//Give Power to Servo Motor holder
//Buttons to move latch and slide
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.Robot;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImpl;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp
public class TeleopTest extends LinearOpMode{

    @Override
    public void runOpMode()  {
        org.firstinspires.ftc.teamcode.Robot robot = new org.firstinspires.ftc.teamcode.Robot (hardwareMap, telemetry);
        robot.isTeleOp = true;

        telemetry.addData ("Status", "Initialized"); //Displays "Status: Initialized"
        telemetry.update();
        //Driver must press INIT and then ▶️

        waitForStart();
        
        while (opModeIsActive()) {
            telemetry.addData("TeleOP", "New Code");
            telemetry.update();

            /********** GamePad 1 ****************/
            //Turning
            if (this.gamepad1.left_stick_x > 0.5) {
                robot.turnForTime(0.6, 10, false, -1 );
            }

            if (this.gamepad1.left_stick_x < -0.5) {
                robot.turnForTime(0.6, 10, false, 1 );
            }

            // Moving
            if (this.gamepad1.right_stick_y > 0.5) {
                robot.moveB(1, 10);
            }

            if (this.gamepad1.right_stick_y < -0.5) {
                robot.moveF(1, 10);
            }

            if (this.gamepad1.right_stick_x > 0.5) {
                robot.moveL(1, 10);
            }

            if (this.gamepad1.right_stick_x < -0.5) {
                robot.moveR(1, 10);
            }

            if(this.gamepad1.dpad_left == true) {
                robot.moveR(0.5, 10);
            }

            if(this.gamepad1.dpad_right == true) {
                robot.moveL(0.5, 10);
            }

            if(this.gamepad1.dpad_up == true) {
                robot.moveF(0.5, 1);
            }

            if(this.gamepad1.dpad_down == true) {
                robot.moveB(0.5, 1);
            }

            /****************** GamePad 2 **************/
            // Pincher
            if (this.gamepad2.x == true) {
                telemetry.addData("Robot-Testing ", "grabStone");
                telemetry.update();
                robot.grabStone1();
            }

            if (this.gamepad2.b == true) {
                telemetry.addData("Robot-Testing ", "dropStone");
                telemetry.update();
                robot.dropStone1();
            }

            // Slide
            if (this.gamepad2.y == true) {
                telemetry.addData("Robot-Testing ", "Slide-Up");
                telemetry.update();
                robot.moveSlideUp(1, 1.25);
            }

            if (this.gamepad2.a == true) {
                telemetry.addData("Robot-Testing ", "Slide-Down");
                telemetry.update();
                robot.moveSlideDown(1, 1.25);
            }

            if (this.gamepad2.dpad_down == true) {
                telemetry.addData("Robot-Testing ", "Slide-Down-slow");
                telemetry.update();
                robot.moveSlideDown(1, 0.2);
            }

            if (this.gamepad2.dpad_up == true) {
                telemetry.addData("Robot-Testing ", "Slide-Down-up");
                telemetry.update();
                robot.moveSlideUp(1, 0.2);
            }

            if (this.gamepad1.b == true){
                telemetry.addData("Clamp", "Up");
                telemetry.update();
                robot.ClampUp(50);
            }

            if (this.gamepad1.x == true){
                telemetry.addData("Clamp", "Down");
                telemetry.update();
                robot.ClampDown(50);
            }
            if (this.gamepad2.right_stick_y > 0.5) {
                robot.moveSlides(1, 10);
            }

            if (this.gamepad2.right_stick_y < -0.5) {
                robot.moveSlides(-1, 10);
            }

        };
    };
}