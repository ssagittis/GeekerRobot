##############################################################################
## Filename:          H:\Xilinx_Xps\openhw_pro\xps_proj/drivers/robot_pwm_ip_v1_00_a/data/robot_pwm_ip_v2_1_0.tcl
## Description:       Microprocess Driver Command (tcl)
## Date:              Fri Jun 26 15:37:10 2015 (by Create and Import Peripheral Wizard)
##############################################################################

#uses "xillib.tcl"

proc generate {drv_handle} {
  xdefine_include_file $drv_handle "xparameters.h" "robot_pwm_ip" "NUM_INSTANCES" "DEVICE_ID" "C_BASEADDR" "C_HIGHADDR" 
}
