##############################################################################
## Filename:          H:\Xilinx_Xps\openhw_pro\xps_proj/drivers/robot_ultrasonic_ip_v1_00_a/data/robot_ultrasonic_ip_v2_1_0.tcl
## Description:       Microprocess Driver Command (tcl)
## Date:              Thu Jul 09 18:03:58 2015 (by Create and Import Peripheral Wizard)
##############################################################################

#uses "xillib.tcl"

proc generate {drv_handle} {
  xdefine_include_file $drv_handle "xparameters.h" "robot_ultrasonic_ip" "NUM_INSTANCES" "DEVICE_ID" "C_BASEADDR" "C_HIGHADDR" 
}
