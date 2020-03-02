/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private RobotContainer m_robotContainer;

  // Declare pneumatic drive motor controllers here
  private final Talon m_right = new Talon(0);
  private final Talon m_left = new Talon(1);
  private final DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);

  // Uncomment the line below when winch lift using Spark is ready
  private final Spark winchLift1 = new Spark(2);
  private final Spark winchLift2 = new Spark(3); //honestly, lift system was super experimental

  // Declare pneumatic pistons here
  private final Solenoid cellDump = new Solenoid(1);
  private final Solenoid cellDump_return = new Solenoid(0);
  private final Solenoid cellOpen = new Solenoid(2);
  private final Solenoid cellClose = new Solenoid(3);

  private final Solenoid scissorLift_Up = new Solenoid(5);
  private final Solenoid scissorLift_Down = new Solenoid(4);

  // SendableChooser<Integer> chooser;

  // Joystick code and any global button assignments go here
  private final Joystick m_stick = new Joystick(0);

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer. This will perform all our button bindings,
    // and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();

    // Camera code goes here
    CameraServer.getInstance().startAutomaticCapture(0);
    CameraServer.getInstance().startAutomaticCapture(1);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler. This is responsible for polling buttons, adding
    // newly-scheduled
    // commands, running already-scheduled commands, removing finished or
    // interrupted commands,
    // and running subsystem periodic() methods. This must be called from the
    // robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your
   * {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();

    }
  }

  /**
   * This function is called periodically during autonomous.
   */

  @Override
  public void autonomousPeriodic() {
    
    //Start Line Mode
    if (Timer.getMatchTime() > 14.5) {
      m_drive.arcadeDrive(-0.5, 0);
      cellOpen.set(false);
      cellClose.set(true);
      cellDump.set(false);
      cellDump_return.set(true);
    } else if (Timer.getMatchTime() <= 14) {
      m_drive.arcadeDrive(0, 0);
    }
    
    /*
    //Dump Mode; values are not final
    if (Timer.getMatchTime() > 12) {
      m_drive.arcadeDrive(-0.5, 0);
      cellOpen.set(false);
      cellClose.set(true);
      cellDump.set(false);
      cellDump_return.set(true);
    } else if (Timer.getMatchTime() < 13 && Timer.getMatchTime() > 10) {
      m_drive.arcadeDrive(0, 0);
      cellOpen.set(true);
      cellClose.set(false);
    } else if(Timer.getMatchTime() < 10 && Timer.getMatchTime() > 7) {
      cellDump.set(true);
      cellDump_return.set(false);
    } else if (Timer.getMatchTime() < 7 && Timer.getMatchTime() > 4) {
      m_drive.arcadeDrive(0.5, 0);
      cellOpen.set(false);
      cellClose.set(true);
      cellDump.set(false);
      cellDump_return.set(true);
    }
    */
}

  
  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    //Pneumatic drive control code goes here; adjust X and Y values if drive is wrong
    m_drive.arcadeDrive(m_stick.getRawAxis(1)*1, m_stick.getRawAxis(0), true);

    //Pneumatic piston command goes here
    if (m_stick.getRawButton(1) == true) {
      cellDump.set(true);
      cellDump_return.set(false);
    } else {
      cellDump.set(false);
      cellDump_return.set(true);
    }

    if (m_stick.getRawButton(2) == true) {
      cellOpen.set(true);
      cellClose.set(false);
    } else {
      cellOpen.set(false);
      cellClose.set(true);
    }



    if(m_stick.getRawButton(10) == true) { //single button scissor lift
      scissorLift_Down.set(false);
      scissorLift_Up.set(true);
    } else if (m_stick.getRawButton(12) == true) { //winch up
      winchLift1.setSpeed(1);
      winchLift2.setSpeed(-1);
    } else if (m_stick.getRawButton(11) == true) { //winch down
      winchLift1.setSpeed(-1);
      winchLift2.setSpeed(1);
    } else {
      winchLift1.setSpeed(0);
      winchLift2.setSpeed(0);
      scissorLift_Down.set(true);
      scissorLift_Up.set(false);
    }
  
  }


  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
    
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
