package pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.util.ArrayList;

public class CharacterLevelResults implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ArrayList<AlignedPair> mOptimalAlignments;
	protected float[][] mCharacterLevelErrors;
	protected float[][] mConfusionMatrix;
	protected float [][] mConfusionMatrixAbsolute;
	protected float[] mTotalErrors;
	protected float[] mAverageErrors;

	public CharacterLevelResults()
	{
		mOptimalAlignments = new ArrayList<AlignedPair>();
	}

	public float getMSDErrorRate()
	{
		if(mAverageErrors.length <= 0) return 0;
		return mAverageErrors[mAverageErrors.length - 1];
	}
	
	public float getInsertionErrorRate()
	{
		if(mAverageErrors.length <= 0) return 0;
		return mAverageErrors[0];
	}
	
	public float getSubstitutionErrorRate()
	{
		if(mAverageErrors.length <= 1) return 0;
		return mAverageErrors[1];
	}
	
	public float getOmissionErrorRate()
	{
		if(mAverageErrors.length <= 2) return 0;
		return mAverageErrors[2];
	}
	
	protected String printCharacterLevelErrors()
	{
		int nchars = mCharacterLevelErrors.length;
		String result = "";
		
		// header for character-level errors table
		result += "Character-Level Errors: MacKenzie & Soukoreff NordiCHI'02\n\n";
		result += "Char\tCount\tIns\tSub\tOmi\tTotal\n";
		
		// print error table
		for(int i=0; i < nchars; i++)
		{
			String line = "";
			
			// first character of line
			if(i == 0)
			{
				// space
				line += "space\t";
			}
			else if(i < nchars - 1)
			{
				// valid letter
				line += ((char)(i+96)) + "\t";
			}
			else
			{
				// insertion character
				line += "-\t";
			}
			
			// print all error types for that character
			for(int j = 0; j < mTotalErrors.length; j++)
			{
				line += mCharacterLevelErrors[i][j];
				if(j < mTotalErrors.length - 1)
					line += "\t";
			}
			result += line + "\n";
		}
		
		// print total line
		String line = "";
		line += "Total\t";
		for (int i = 0; i < mTotalErrors.length; i++)
        {
            line += mTotalErrors[i];
            if (i < mTotalErrors.length - 1)
                line += "\t";
        }
		result += line + "\n";
		
		// print average line
		line = "";
		line += "Average\t\t";
		for (int i = 0; i < mAverageErrors.length; i++)
        {
            line += mAverageErrors[i];
            if (i < mAverageErrors.length - 1)
                line += "\t";
        }
		result += line + "\n";
		
		return result;
	}
	
	protected String printConfusionMatrix()
	{
		int nchars = mCharacterLevelErrors.length;
		String result = "";
		
		// print confusion matrix
		result += "\n";
		result += "Substitutions Confusion Matrix\n";
		result += "Axes: X = required, Y = transcribed\n\n";
		
		// header, required
		result += "\t";
		for (int i = 0; i < nchars; i++)
        {
            if (i == 0)
            {
            	// space
                result += "space";
            }
            else if (i == nchars - 1)
            {
            	// unknown character
            	result += "�";
            }
            else
            {
            	// valid letter
            	result += (char)(i + 96);
            }
            
            if (i < nchars - 1)
                result += "\t";
        }
        result += "\n";
        
        for (int i = 0; i < nchars; i++)
        {
        	// transcribed char
            
            if (i == 0)
            {
            	// space
                result += "space\t";
            }
            else if (i == nchars - 1)
            {
            	// unknown character
                result += "�\t";
            }
            else
            {
                result += (char)(i + 96) + "\t";
            }
            for (int j = 0; j < nchars; j++)
            {
                result += mConfusionMatrix[i][j];
                if (j < nchars - 1)
                    result += "\t";
            }
            result += "\n";
        }
		        
		return result;
	}
	
	public String printKeyPressErrors(String participant)
	{
		int nchars = mCharacterLevelErrors.length;
		String result = "";
		
		// for each required character, check all transcribed characters
		for (int i = 0; i < nchars; i++)
        {
			String required = getChar(i);
			float[] transcribedChords = mConfusionMatrixAbsolute[i];
			
			// for all transcribed chords in required[i]
			for(int j = 0; j < transcribedChords.length; j++)
			{
				String transcribed = getChar(j);
				int nKeyPresses = (int) mConfusionMatrixAbsolute[i][j];
				
				for(int n = 0; n < nKeyPresses; n++)
				{
					result += participant + "\t" + required + "\t" + transcribed + "\n";
				}
				
			}
        }
		
		return result;
	}
	
	public String toString()
	{
		String result = "";
		
		result += printCharacterLevelErrors();
        result += printConfusionMatrix();
        //result += printKeyPressErrors();
        
		return result;
	}
	
	protected String getChar(int i)
	{
		if (i == 0)
        {
        	// space
            return "space";
        }
        else if (i == mCharacterLevelErrors.length - 1)
        {
        	// unknown character
        	return "�";
        }
        else
        {
        	// valid letter
        	return String.valueOf((char)(i + 96));
        }
	}
	
	/*
	 * Getters and Setters
	 */
	
	public float[] getTotalErrors() {
		return mTotalErrors;
	}

	public void setTotalErrors(float[] totalErrors) {
		mTotalErrors = totalErrors;
	}

	public float[] getAverageErrors() {
		return mAverageErrors;
	}

	public void setAverageErrors(float[] averageErrors) {
		mAverageErrors = averageErrors;
	}

	public ArrayList<AlignedPair> getOptimalAlignments() {
		return mOptimalAlignments;
	}

	public void setOptimalAlignments(ArrayList<AlignedPair> alignments) {
		mOptimalAlignments = alignments;
	}

	public float[][] getCharacterLevelErrors() {
		return mCharacterLevelErrors;
	}

	public void setCharacterLevelErrors(float[][] characterLevelErrors) {
		mCharacterLevelErrors = characterLevelErrors;
	}

	public float[][] getConfusionMatrix() {
		return mConfusionMatrix;
	}

	public void setConfusionMatrix(float[][] confusionMatrix) {
		mConfusionMatrix = confusionMatrix;
	}
	
	public float[][] getConfusionMatrixAbsolute()
	{
		return mConfusionMatrixAbsolute;
	}
	
	public void setConfusionMatrixAbsolute(float[][] confusionMatrixAbsolute)
	{
		mConfusionMatrixAbsolute = confusionMatrixAbsolute;
	}
}
