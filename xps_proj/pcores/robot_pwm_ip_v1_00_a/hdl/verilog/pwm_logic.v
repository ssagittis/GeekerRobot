`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date:    00:08:20 05/25/2015 
// Design Name: 
// Module Name:    pwm_logic 
// Project Name: 
// Target Devices: 
// Tool versions: 
// Description: 
//
// Dependencies: 
//
// Revision: 
// Revision 0.01 - File Created
// Additional Comments: 
//
//////////////////////////////////////////////////////////////////////////////////
module pwm_logic(
		pwm_clk,
		pwm_rst,
		pwm_out,
		pwm_period,
		pwm_duty
    );
	
	parameter reg_width = 32;
	
	input  pwm_clk;
	input  pwm_rst;
	output pwm_out;
	input  [reg_width-1:0] pwm_period;
	input  [reg_width-1:0] pwm_duty;
	
	reg [reg_width-1:0] pwm_cnt;
	assign pwm_out = (pwm_cnt < pwm_duty)? 1:0;
	wire   restart = (pwm_cnt == pwm_period);
	
	always@(posedge pwm_clk) begin
		if((~pwm_rst)||restart) begin
			pwm_cnt <= 0;
		end
		else begin
			pwm_cnt <= pwm_cnt + 1;
		end
	end

	
endmodule
