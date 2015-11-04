/*****************************************************************************
* Filename:          H:\Xilinx_Xps\openhw_pro\xps_proj/drivers/robot_ultrasonic_ip_v1_00_a/src/robot_ultrasonic_ip.h
* Version:           1.00.a
* Description:       robot_ultrasonic_ip Driver Header File
* Date:              Thu Jul 09 18:03:58 2015 (by Create and Import Peripheral Wizard)
*****************************************************************************/

#ifndef ROBOT_ULTRASONIC_IP_H
#define ROBOT_ULTRASONIC_IP_H

/***************************** Include Files *******************************/

#include "xbasic_types.h"
#include "xstatus.h"
#include "xil_io.h"

/************************** Constant Definitions ***************************/


/**
 * User Logic Slave Space Offsets
 * -- SLV_REG0 : user logic slave module register 0
 * -- SLV_REG1 : user logic slave module register 1
 */
#define ROBOT_ULTRASONIC_IP_USER_SLV_SPACE_OFFSET (0x00000000)
#define ROBOT_ULTRASONIC_IP_SLV_REG0_OFFSET (ROBOT_ULTRASONIC_IP_USER_SLV_SPACE_OFFSET + 0x00000000)
#define ROBOT_ULTRASONIC_IP_SLV_REG1_OFFSET (ROBOT_ULTRASONIC_IP_USER_SLV_SPACE_OFFSET + 0x00000004)

/**************************** Type Definitions *****************************/


/***************** Macros (Inline Functions) Definitions *******************/

/**
 *
 * Write a value to a ROBOT_ULTRASONIC_IP register. A 32 bit write is performed.
 * If the component is implemented in a smaller width, only the least
 * significant data is written.
 *
 * @param   BaseAddress is the base address of the ROBOT_ULTRASONIC_IP device.
 * @param   RegOffset is the register offset from the base to write to.
 * @param   Data is the data written to the register.
 *
 * @return  None.
 *
 * @note
 * C-style signature:
 * 	void ROBOT_ULTRASONIC_IP_mWriteReg(Xuint32 BaseAddress, unsigned RegOffset, Xuint32 Data)
 *
 */
#define ROBOT_ULTRASONIC_IP_mWriteReg(BaseAddress, RegOffset, Data) \
 	Xil_Out32((BaseAddress) + (RegOffset), (Xuint32)(Data))

/**
 *
 * Read a value from a ROBOT_ULTRASONIC_IP register. A 32 bit read is performed.
 * If the component is implemented in a smaller width, only the least
 * significant data is read from the register. The most significant data
 * will be read as 0.
 *
 * @param   BaseAddress is the base address of the ROBOT_ULTRASONIC_IP device.
 * @param   RegOffset is the register offset from the base to write to.
 *
 * @return  Data is the data from the register.
 *
 * @note
 * C-style signature:
 * 	Xuint32 ROBOT_ULTRASONIC_IP_mReadReg(Xuint32 BaseAddress, unsigned RegOffset)
 *
 */
#define ROBOT_ULTRASONIC_IP_mReadReg(BaseAddress, RegOffset) \
 	Xil_In32((BaseAddress) + (RegOffset))


/**
 *
 * Write/Read 32 bit value to/from ROBOT_ULTRASONIC_IP user logic slave registers.
 *
 * @param   BaseAddress is the base address of the ROBOT_ULTRASONIC_IP device.
 * @param   RegOffset is the offset from the slave register to write to or read from.
 * @param   Value is the data written to the register.
 *
 * @return  Data is the data from the user logic slave register.
 *
 * @note
 * C-style signature:
 * 	void ROBOT_ULTRASONIC_IP_mWriteSlaveRegn(Xuint32 BaseAddress, unsigned RegOffset, Xuint32 Value)
 * 	Xuint32 ROBOT_ULTRASONIC_IP_mReadSlaveRegn(Xuint32 BaseAddress, unsigned RegOffset)
 *
 */
#define ROBOT_ULTRASONIC_IP_mWriteSlaveReg0(BaseAddress, RegOffset, Value) \
 	Xil_Out32((BaseAddress) + (ROBOT_ULTRASONIC_IP_SLV_REG0_OFFSET) + (RegOffset), (Xuint32)(Value))
#define ROBOT_ULTRASONIC_IP_mWriteSlaveReg1(BaseAddress, RegOffset, Value) \
 	Xil_Out32((BaseAddress) + (ROBOT_ULTRASONIC_IP_SLV_REG1_OFFSET) + (RegOffset), (Xuint32)(Value))

#define ROBOT_ULTRASONIC_IP_mReadSlaveReg0(BaseAddress, RegOffset) \
 	Xil_In32((BaseAddress) + (ROBOT_ULTRASONIC_IP_SLV_REG0_OFFSET) + (RegOffset))
#define ROBOT_ULTRASONIC_IP_mReadSlaveReg1(BaseAddress, RegOffset) \
 	Xil_In32((BaseAddress) + (ROBOT_ULTRASONIC_IP_SLV_REG1_OFFSET) + (RegOffset))

/************************** Function Prototypes ****************************/


/**
 *
 * Run a self-test on the driver/device. Note this may be a destructive test if
 * resets of the device are performed.
 *
 * If the hardware system is not built correctly, this function may never
 * return to the caller.
 *
 * @param   baseaddr_p is the base address of the ROBOT_ULTRASONIC_IP instance to be worked on.
 *
 * @return
 *
 *    - XST_SUCCESS   if all self-test code passed
 *    - XST_FAILURE   if any self-test code failed
 *
 * @note    Caching must be turned off for this function to work.
 * @note    Self test may fail if data memory and device are not on the same bus.
 *
 */
XStatus ROBOT_ULTRASONIC_IP_SelfTest(void * baseaddr_p);
/**
*  Defines the number of registers available for read and write*/
#define TEST_AXI_LITE_USER_NUM_REG 2


#endif /** ROBOT_ULTRASONIC_IP_H */
