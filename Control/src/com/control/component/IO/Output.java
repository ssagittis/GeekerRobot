package com.control.component.IO;

public abstract class Output {
	private int dataType;
	Input inputBind=null;//这个绑定目前只为了在绑定时文字显示红色，没其他什么用
	public int getDataType() {
		return dataType;
	}
	public Output(int dataType){
		this.dataType=dataType;
	}
	public void bindInput(Input input){
		input.outputBind=this;
		inputBind=input;
	}
	public void unbindInput(){
		unbindProcess();
		if(inputBind!=null){
			inputBind.outputBind=null;
			inputBind=null;
		}
	}
	public boolean isBind(){
		if(inputBind==null) return false;
		else return true;
	}
	public abstract Object getOutputData() throws NoInputException;
	protected abstract void unbindProcess();
}
