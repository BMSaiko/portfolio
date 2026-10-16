# Connecting to a Serial Port Using WSL

## Index
- [Prerequisites](#prerequisites)
- [Updating WSL](#updating-wsl)
- [Identifying COM Port in Windows](#identifying-com-port-in-windows)
- [Mapping COM Ports to WSL Devices](#mapping-com-ports-to-wsl-devices)
- [Configuring Permissions in WSL](#configuring-permissions-in-wsl)
- [Installing Serial Communication Tools in WSL](#installing-serial-communication-tools-in-wsl)
- [Troubleshooting Tips](#troubleshooting-tips)
- [Additional Resources](#additional-resources)

## Prerequisites

Before starting, make sure you meet the following requirements:

- WSL Installed: Have WSL installed on your Windows computer. WSL 2 is recommended for better performance and compatibility.
- Serial Device Available: Connect a serial device to your computer, identified by a COM port (e.g., COM3).
- Administrative Access: Administrative privileges may be required to configure certain settings.

## Updating WSL

It's important to ensure you're using the latest version of WSL to take advantage of the latest features and bug fixes.

### a. Exit WSL Environment

If you're inside the WSL terminal, exit it to return to the Windows environment:

exit

### b. Open PowerShell with Administrative Privileges

1. Press Win + X and select "Windows PowerShell (Admin)" or "Terminal (Admin)".
2. Confirm the User Account Control (UAC) prompt if it appears.

### c. Update WSL

In elevated PowerShell, run the following command to update WSL:

wsl --update

> Note: This command updates the WSL kernel to the latest available version.

### d. Verify WSL Version

After updating, check WSL status:

wsl --status

This will display information about the WSL version, kernel, and other relevant settings.

### e. Restart WSL

To apply updates, shut down all WSL instances:

wsl --shutdown

Then reopen the WSL terminal.

## Identifying COM Port in Windows

Before mapping the port to WSL, identify which COM port your serial device is using.

### Steps to Identify COM Port:

1. Open Device Manager:
   - Press Win + X and select "Device Manager".

2. Locate COM Port:
   - Expand "Ports (COM & LPT)" section.
   - Identify the listed serial device (e.g., "USB Serial Port (COM3)").
   - Note the COM port number (in this example, COM3).

## Mapping COM Ports to WSL Devices

In WSL, Windows COM ports are mapped to Linux /dev/ttyS* devices according to the following correspondence:

| COM Port (Windows) | WSL Device (/dev/ttyS*) |
|-------------------|-------------------------|
| COM1 | /dev/ttyS0 |
| COM2 | /dev/ttyS1 |
| COM3 | /dev/ttyS2 |
| COM4 | /dev/ttyS3 |
| ... | ... |

> Example: If your device is on COM3, it will be accessible in WSL through /dev/ttyS2.

## Configuring Permissions in WSL

To access serial devices in WSL, your user needs appropriate permissions.

### a. Add User to dialout Group

The dialout group typically has permissions to access serial devices.

sudo adduser $USER dialout

> Note: After running this command, you'll need to restart your WSL session or log out and log back in for the changes to take effect.

### b. Alternatively, Modify Device Permissions (Temporary)

If you prefer, you can change the device permissions directly. This change is temporary and will be reset after restarting WSL.

sudo chmod 666 /dev/ttyS2

> Warning: Changing permissions can pose security risks. Use with caution.

## Installing Serial Communication Tools in WSL

To interact with the serial port, you can use tools like screen, minicom, or picocom. Here's how to install and use each one.

### Using screen

screen is a versatile tool for managing terminal sessions, including serial communication.

1. Install screen
   
   sudo apt update
   sudo apt install screen
   

2. Connect to Serial Port
   
   screen /dev/ttyS2 115200
   

3. Exit screen
   - Press Ctrl + A followed by K and confirm with Y.

### Using minicom

minicom is a serial communication program that offers a more user-friendly interface.

1. Install minicom
   
   sudo apt update
   sudo apt install minicom
   

2. Configure minicom
   
   sudo minicom -s
   

   Configuration Steps:
   - Navigate to "Serial port setup".
   - Set Serial Device to /dev/ttyS2.
   - Configure Bps/Par/Bits as needed (e.g., 115200 8N1).
   - Save settings and exit configuration.

3. Start minicom
   
   minicom
   

4. Exit minicom
   - Press Ctrl + A followed by X to end the session.

### Using picocom

picocom is a lightweight tool for serial communication, ideal for quick use.

1. Install picocom
   
   sudo apt update
   sudo apt install picocom
   

2. Connect to Serial Port
   
   picocom -b 115200 /dev/ttyS2
   

3. Exit picocom
   - Press Ctrl + A followed by Ctrl + X.

## Troubleshooting Tips

### a. Port Already in Use
- Solution: Make sure no Windows applications (like PuTTY or Arduino IDE) are using the COM port you want to access in WSL.

### b. Permission Denied Errors
- Solution: Check if your user is in the dialout group or adjust device permissions as mentioned earlier.

### c. Device Not Found
- Solution: Verify the correct mapping of COM port to WSL device. For example, COM3 corresponds to /dev/ttyS2.

### d. Baud Rate Mismatch
- Solution: Ensure the transmission rate configured in the serial tool matches the one required by the device.

### e. Restart WSL
- Solution: Sometimes, restarting WSL can resolve connectivity issues.
  
  wsl --shutdown
  
  Then reopen the WSL terminal.

## Additional Resources

- [Official Microsoft WSL Documentation on Serial Port Access]()
- [Minicom Documentation]()
- [Screen Documentation]()

## Conclusion

Following this guide, you'll be able to configure and use a serial port in the WSL environment, taking advantage of the powerful tools available in Linux directly on your Windows system. If you encounter additional difficulties, feel free to seek more information in the mentioned resources or request additional assistance.
