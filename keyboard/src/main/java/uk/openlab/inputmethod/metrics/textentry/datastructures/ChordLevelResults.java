package uk.openlab.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;

public class ChordLevelResults extends CharacterLevelResults implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static String[] mChordValues = {"space", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", 
			"q", "r", "s", "t", "u", "v", "w", "x", "y", "z", 
			"#", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", 
			"'", ",",
			"=", "&", "!", ")", "(", "]", "[", "<", ">", ":", "\\", "/", "*", "$", "%", "?", "+", "-", "@", "^", "_", "\"", ".",
			";", "�"};
	
	protected static String[] mChordCodes = {"------","100000","110000","100100","100110" ,"100010","110100","110110","110010","010100","010110","101000","111000","101100","101110","101010","111100",
			"111110","111010","011100","011110","101001","111001","010111","101101","101111","101011", //z
			"001111","010000","011000","010010","010011","010001","011010","011011","011001","001010","001011",
			"001000","000001","111111","111101","011101","011111","111011","110111","010101","110001","001110"
			,"100011","110011","001100","100001","110101","100101","100111","001101","001001","000100","000110",
			"000111","000010","000101","000011", "??????"};
	
	public ChordLevelResults()
	{
		super();
	}
	
	@Override
	protected String printConfusionMatrix()
	{
		int nchords = mConfusionMatrix.length;
		String result = "";
		
		// print confusion matrix
		result += "\n";
		result += "Substitutions Confusion Matrix\n";
		result += "Axes: X = transcribed, Y = required\n\n";
		
		// header, transcribed
		result += "\t";
		for (int i = 0; i < nchords; i++)
        {
			// get string value
			result += mChordValues[i];
            
            if (i < nchords - 1)
                result += "\t";
        }
        result += "\n";
        
        for (int i = 0; i < nchords; i++)
        {
        	// required char
            result += mChordValues[i] + "\t";
            
            for (int j = 0; j < nchords; j++)
            {
                result += mConfusionMatrix[i][j];
                if (j < nchords - 1)
                    result += "\t";
            }
            result += "\n";
        }
		        
		return result;
	}
	
	@Override
	public String printKeyPressErrors(String participant)
	{
		int nchords = mConfusionMatrixAbsolute.length;
		String result = "";
		
		// print keypress table
		
		// for each required character, check all transcribed characters
		for (int i = 0; i < nchords; i++)
        {
			String required = mChordValues[i];
			float[] transcribedChords = mConfusionMatrixAbsolute[i];
			
			// for all transcribed chords in required[i]
			for(int j = 0; j < transcribedChords.length; j++)
			{
				String transcribed = mChordValues[j];
				int nKeyPresses = (int) mConfusionMatrixAbsolute[i][j];
				
				for(int n = 0; n < nKeyPresses; n++)
				{
					String transcribedCode = getCode(transcribed);
					String requiredCode = getCode(required);
					
					// ignore blank spaces (its not a chord, its a gesture)
					if(transcribedCode.equalsIgnoreCase(mChordCodes[0]) || requiredCode.equalsIgnoreCase(mChordCodes[0])) 
						continue;
					
					result += participant + "\t" + required + "\t" + requiredCode + "\t" + 
							transcribed + "\t" + transcribedCode + "\n";
				}
				
			}
        }
		
		return result;
	}
	
	public static String printCodingTable()
	{
		String result = "Code\tCharacter\n";
		for(int i = 0; i < mChordCodes.length; i++)
			result += mChordCodes[i] + "\t" + mChordValues[i] + "\n";
		return result;
	}
	
	protected String getCode(String c)
	{
		String code = "------";
		
		for(int i = 0; i < mChordValues.length; i++)
			if(c.equalsIgnoreCase(mChordValues[i]))
				return mChordCodes[i];
		
		return code;
	}

}
