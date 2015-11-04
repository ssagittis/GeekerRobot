module steer(
    input mclk,
    input[7:0] angle,
    output reg pwm,
    input[31:0] clk_hz //°å×ÓµÄ¾§ÕñÆµÂÊ
    );
  
    //parameter MCLK = 125000000;  //é»˜è®¤æ˜?0Mhzï¼Œè‹¥ä¸æ˜¯ï¼Œä»é¡¶å±‚æ”¹å‚æ•?    
    //wire[31:0] CNT = clk_hz/100000;
	 
	parameter CNT = 1000;
    
    reg[15:0] angle_t = 0;
    reg[31:0] pwmcnt = 0;
    reg[31:0] clkcnt = 0;
    
    always@(posedge mclk) begin
        if(clkcnt<CNT) begin
            clkcnt <= clkcnt+1;
        end
        else begin
            clkcnt <= 0;
        end
    end
    
    always@(posedge mclk) begin
        if(clkcnt == 0) begin
            if(pwmcnt == 0) begin
               //angle_t = 200*angle/180;
			   //angle_t = angle;
					angle_t <= angle + angle[7];
               pwm <= 1;
               pwmcnt <= pwmcnt+1;
            end
            else if(pwmcnt<2000) begin
                pwmcnt <= pwmcnt+1;
                if(pwmcnt >= 50+angle_t[7:0]) begin
                    pwm <= 0;
                end
            end
            else begin
                pwmcnt <= 0;      
            end
        end
    end
	
endmodule