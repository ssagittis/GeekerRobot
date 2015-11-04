package com.control.component.IO;

public abstract class Input{
	Output outputBind=null;
	public Output getOutputBind()throws NoInputException{
		if(outputBind==null) throw new NoInputException();
		return outputBind;
	}
	private int dataType;
	public int getDataType() {
		return dataType;
	}
	public Input(int dataType){
		this.dataType=dataType;
	}
	//public boolean getDataType() {
	//	return dataType;
	//}
	//public void setDataType(boolean dataType) {
	//	this.dataType = dataType;
	//}
	public void bindOutput(Output output){
		//if(output.getOutputData().getType()==dataType){
			outputBind=output;
			output.inputBind=this;
		//	return true;
		//}else{
		//	return false;
		//}
	}
	public void unbindOutput(){
		unbindProcess();
		if(outputBind!=null){
			outputBind.inputBind=null;
			outputBind=null;
		}
	}
	public boolean isBind(){
		if(outputBind==null) return false;
		else return true;
	}
	protected abstract void unbindProcess();
}
