module ultrasonic(
	input mclk,
	input rst_n,
	input en,
	output reg trig,
	input echo,
	output reg[31:0] distance
);
	parameter MCLK = 100000000;
	parameter US = MCLK/1000000;
	parameter TRIGPEIOD =5000;//发�?trig信号的周�?等待60ms
	
	//uscnt 用来分频主时�?
	reg[31:0] uscnt;
	always@(posedge mclk) begin
		if(!rst_n) begin
			uscnt <= 0;
		end
		else begin
			if(en) begin
				if(uscnt<US) begin
					uscnt <= uscnt+1;
				end
				else begin
					uscnt <= 0;
				end
			end
		end
	end
	
	//usclk +1 代表 1us
	reg [31:0] usclk;
	always@(posedge mclk) begin
		if(!rst_n) begin
			usclk <= 0;
		end
		else begin
			if(en) begin
				if(uscnt == US-1) begin
					if(usclk < TRIGPEIOD) begin
						usclk <= usclk + 1;
					end
					else begin
						usclk <= 0;
					end
				end
			end
		end
	end
	
	//trig 10us
	always@(posedge mclk) begin
		if(!rst_n) begin
			trig <= 0;
		end
		else begin
			if(en) begin
				if(usclk == 0) begin
					trig <= 1;
				end
				else if(usclk == 20) begin
					trig <= 0;
				end
			end
		end
	end
	
	//得到echo信号高电平的长度
	reg[31:0] echocnt;
	always@(posedge mclk) begin
		if(!rst_n) begin
			echocnt  <= 0;
		end
		else begin
			if(en) begin
				if(echo == 1) begin
					echocnt <= echocnt + 1;
				end
				if(usclk == 0) begin
					echocnt <= 0;
				end
			end 
		end
	end
	
	//reg[31:0] dis = 0;
	//转换成距离输�?
	always@(posedge mclk) begin
		if(!rst_n) begin
			distance <= 0;//0就代表无限远
		end
		else begin
			if(en) begin 
				if(usclk == TRIGPEIOD) begin
					distance <=echocnt;
					//dis <= echocnt*340/2*1000/125000000;
				end
			end
		end
	//distance <= 32'b11111111111111111111111111111111;	
	end
	
endmodule