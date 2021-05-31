package pt.lasige.demo.inputmethod.metrics.textentry.datastructures;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ISCharacterLevelResults implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum ErrRateType { UNCORRECTED, CORRECTED, NONRECOGNITION, TOTAL, COUNT };
	public enum ErrType { NOERROR, SUBSTITUTION, INSERTION, OMISSION };
	
	// number of chars supported by the analysis: 
	// 26 lower case letters
	// 26 upper case letters
	// space
	// unrecognized
	// other chars
	// total = 55
	public final static int NCHARS = 55;
	
	static final private char UNRECOGNIZED_CHAR = 'ø';
	static final private char OTHER_CHAR = 'º';
		
	// alignments and MSD
	protected ArrayList<StreamAlignment> mStreamAlignments = new ArrayList<StreamAlignment>();
	protected TextEntryMSD mMSD = new TextEntryMSD();
	
	// character errors
	protected ErrorRateResults[] mSubstitutionErrors = new ErrorRateResults[NCHARS];
	protected ErrorRateResults[] mInsertionErrors = new ErrorRateResults[NCHARS];
	protected ErrorRateResults[] mOmissionErrors = new ErrorRateResults[NCHARS];
	protected ErrorRateResults[] mNoErrors = new ErrorRateResults[NCHARS];
	
	// overall errors
	protected ErrorRateResults[] mOverallErrors = new ErrorRateResults[NCHARS];
	
	// character count
	protected float[] mPresented = new float[NCHARS];
	protected float[] mTranscribed = new float[NCHARS];
	protected float[] mEntered = new float[NCHARS]; // transcribed + corrected (deleted)
	protected float[] mIntended = new float[NCHARS];
	protected float[] mCorrect = new float[NCHARS];
	protected float[] mNR = new float[NCHARS]; //non-recognitions
	
	protected float[][] mConfusionMatrixCount = new float[NCHARS][NCHARS];
	
	public ISCharacterLevelResults()
	{
		for(int i = 0; i < NCHARS; i++)
		{
			mSubstitutionErrors[i] = new ErrorRateResults();
			mInsertionErrors[i] = new ErrorRateResults();
			mOmissionErrors[i] = new ErrorRateResults();
			mNoErrors[i] = new ErrorRateResults();
			mOverallErrors[i] = new ErrorRateResults();
			
			mPresented[i] = 0;
			mTranscribed[i] = 0;
			mEntered[i] = 0;
			mIntended[i] = 0;
			mCorrect[i] = 0;
			mNR[i] = 0;
			
			for(int j = 0; j < NCHARS; j++)
				mConfusionMatrixCount[i][j] = 0;
		}
	}
	
	/**
	 *  Add Methods
	 */
	public void addCharacterError(ErrRateType errRateType, ErrType errType, char intended, char entered)
	{
		addCharacterError(errRateType, errType, intended, entered, new InputAction());
	}
	
	@SuppressWarnings("incomplete-switch")
	public void addCharacterError(ErrRateType errRateType, ErrType errType, char intended, char entered, InputAction action)
	{
		switch(errRateType)
		{
		case UNCORRECTED:
			
			switch(errType)
			{
			case NOERROR:
				//System.out.println("uncorrected noerror (" + intended + ", " + entered + ")");
				mNoErrors[getIndex(entered)].addUncCount();
				
				mConfusionMatrixCount[getIndex(intended)][getIndex(entered)]++;
				
				mTranscribed[getIndex(entered)]++;
				mEntered[getIndex(entered)]++;
				mIntended[getIndex(intended)]++;
				mCorrect[getIndex(entered)]++;
				
				action.Intended.add(String.valueOf(intended));
				break;
				
			case SUBSTITUTION:
				//System.out.println("uncorrected substitution (" + intended + ", " + entered + ")");
				mSubstitutionErrors[getIndex(intended)].addUncCount();
				
				mConfusionMatrixCount[getIndex(intended)][getIndex(entered)]++;
				
				mTranscribed[getIndex(entered)]++;
				mEntered[getIndex(entered)]++;
				mIntended[getIndex(intended)]++;
				
				action.Intended.add(String.valueOf(intended));
				break;
				
			case INSERTION:
				//System.out.println("uncorrected insertion (" + intended + ", " + entered + ")");
				mInsertionErrors[getIndex(entered)].addUncCount();
				
				mTranscribed[getIndex(entered)]++;
				mEntered[getIndex(entered)]++;
				break;
				
			case OMISSION:
				//System.out.println("uncorrected omission (" + intended + ", " + entered + ")");
				mOmissionErrors[getIndex(intended)].addUncCount();
				break;
			}
			
			break;
			
		case CORRECTED:
			
			switch(errType)
			{
			case NOERROR:
				//System.out.println("corrected noerror (" + intended + ", " + entered + ")");
				mNoErrors[getIndex(entered)].addCorrCount();
				
				mConfusionMatrixCount[getIndex(intended)][getIndex(entered)]++;
				
				mEntered[getIndex(entered)]++;
				mIntended[getIndex(intended)]++;
				mCorrect[getIndex(entered)]++;
				
				action.Intended.add(String.valueOf(intended));
				break;
				
			case SUBSTITUTION:
				//System.out.println("corrected substitution (" + intended + ", " + entered + ")");
				mSubstitutionErrors[getIndex(intended)].addCorrCount();
				
				mConfusionMatrixCount[getIndex(intended)][getIndex(entered)]++;
				
				mEntered[getIndex(entered)]++;
				mIntended[getIndex(intended)]++;
				
				action.Intended.add(String.valueOf(intended));
				break;
				
			case INSERTION:
				//System.out.println("corrected insertion (" + intended + ", " + entered + ")");
				mInsertionErrors[getIndex(entered)].addCorrCount();
				
				mEntered[getIndex(entered)]++;
				break;
				
			case OMISSION:
				//System.out.println("corrected omission (" + intended + ", " + entered + ")");
				mOmissionErrors[getIndex(intended)].addCorrCount();
				break;
			}
			
			break;
			
		case NONRECOGNITION:
			
			switch(errType)
			{
			case SUBSTITUTION:
				//System.out.println("nonrecognition substitution (" + intended + ", " + entered + ")");
				mSubstitutionErrors[getIndex(intended)].addNonRecCount();
			
				mEntered[getIndex(entered)]++;
				mIntended[getIndex(intended)]++;
				
				action.Intended.add(String.valueOf(intended));
				break;
				
			case INSERTION:
				//System.out.println("nonrecognition insertion (" + intended + ", " + entered + ")");
				mInsertionErrors[getIndex(entered)].addNonRecCount(); // this is never used to calculate anything because NR are never transcribed
				
				mEntered[getIndex(entered)]++;
				break;
			}
			
			break;
		}
	}
	
	public void computePresented(String P)
	{
		for(char c : P.toCharArray())
		{
			mPresented[getIndex(c)]++;
		}
	}
	
	public void computeErrors()
	{
		for(int i = 0; i < NCHARS; i++)
		{
			// overall error rate
			float uncNoErrors = mNoErrors[i].getUncCount();
			float corrNoErrors = mNoErrors[i].getCorrCount();
			float transc = mTranscribed[i];
			float entered = mEntered[i];
			
			mOverallErrors[i].setUnc(notNaN(1 - (uncNoErrors / transc)));
			mOverallErrors[i].setCorr(notNaN(1 - (corrNoErrors / (entered - transc))));
			mOverallErrors[i].setTotal(notNaN(1 - ((uncNoErrors + corrNoErrors) / entered)));
			
			// substitution error rate
			float uncSubs = mSubstitutionErrors[i].getUncCount();
			float corrSubs = mSubstitutionErrors[i].getCorrCount();
			float nrSubs = mSubstitutionErrors[i].getNRCount();
			float intended = mIntended[i];
			
			mSubstitutionErrors[i].setUnc(notNaN(uncSubs / intended));
			mSubstitutionErrors[i].setCorr(notNaN(corrSubs / intended));
			mSubstitutionErrors[i].setNR(notNaN(nrSubs / intended));
			mSubstitutionErrors[i].setTotal(notNaN((uncSubs + corrSubs + nrSubs)/intended));
			
			// omission error rate
			float uncOmi = mOmissionErrors[i].getUncCount();
			float corrOmi = mOmissionErrors[i].getCorrCount();
			float presented = mPresented[i];
			
			mOmissionErrors[i].setUnc(notNaN(uncOmi / presented));
			mOmissionErrors[i].setCorr(notNaN(corrOmi / presented));
			mOmissionErrors[i].setTotal(notNaN((uncOmi + corrOmi) / presented));
			
			// insertion error rate
			float uncIns = mInsertionErrors[i].getUncCount();
			float corrIns = mInsertionErrors[i].getCorrCount();
			float nrIns = mInsertionErrors[i].getNRCount();

			mInsertionErrors[i].setUnc(notNaN(uncIns / entered));
			mInsertionErrors[i].setCorr(notNaN(corrIns / entered));
			mInsertionErrors[i].setNR(notNaN(nrIns / entered));
			mInsertionErrors[i].setTotal(notNaN((uncIns + corrIns + nrIns) / entered));
		}
	}
	
	public void averageErrorRateCounts(float n)
	{
		for(int i = 0; i < NCHARS; i++)
		{
			mSubstitutionErrors[i].setCorrCount(notNaN(mSubstitutionErrors[i].getCorrCount() / n));
			mSubstitutionErrors[i].setUncCount(notNaN(mSubstitutionErrors[i].getUncCount() / n));
			mSubstitutionErrors[i].setNRCount(notNaN(mSubstitutionErrors[i].getNRCount() / n));
			
			mInsertionErrors[i].setCorrCount(notNaN(mInsertionErrors[i].getCorrCount() / n));
			mInsertionErrors[i].setUncCount(notNaN(mInsertionErrors[i].getUncCount() / n));
			mInsertionErrors[i].setNRCount(notNaN(mInsertionErrors[i].getNRCount() / n));
			
			mOmissionErrors[i].setCorrCount(notNaN(mOmissionErrors[i].getCorrCount() / n));
			mOmissionErrors[i].setUncCount(notNaN(mOmissionErrors[i].getUncCount() / n));
			mOmissionErrors[i].setNRCount(notNaN(mOmissionErrors[i].getNRCount() / n));
			
			mNoErrors[i].setCorrCount(notNaN(mNoErrors[i].getCorrCount() / n));
			mNoErrors[i].setUncCount(notNaN(mNoErrors[i].getUncCount() / n));
			mNoErrors[i].setNRCount(notNaN(mNoErrors[i].getNRCount() / n));
			
			for(int j = 0; j < NCHARS; j++)
				mConfusionMatrixCount[i][j] /= n;
		}
	}
	
	public void averageCharacterCounts(float n)
	{
		for(int i = 0; i < NCHARS; i++)
		{
			mPresented[i] /= n;
			mTranscribed[i] /= n;
			mEntered[i] /= n;
			mIntended[i] /= n;
			mCorrect[i] /= n;
			mNR[i] /= n;
		}
	}
	
	private float notNaN(float n)
	{
		
		if(Float.isNaN(n)) return 0f;
		else return n;
	}
	
	private int getIndex(char c)
	{
		if(c >= 'A' && c <= 'Z')
		{
			return c - 65;
		}
		else if(c >= 'a' && c <= 'z')
		{
			return c - 97 + 26;
		}
		else if(c == ' ')
			return 52;
		else if(c == UNRECOGNIZED_CHAR)
			return 53; // unrecognized
		else
			return 54; // other char
	}
	
	private char getChar(int i)
	{
		if(i < 26)
			return (char)(i + 65);
		else if(i >= 26 && i < 52)
			return (char)(i - 26 + 97);
		else if(i == 52)
			return ' ';
		else if(i == 53)
			return UNRECOGNIZED_CHAR;
		else
			return OTHER_CHAR;
	}
	
	public String printCharacterLevelErrors()
	{
		String result = "";
		DecimalFormat df = new DecimalFormat("####.000");
		
		// header for character-level errors table
		result += "Character-Level Errors: Wobbrock & Myers TOCHI'06\n";
		result += "\tCharacter Counts\t\t\t\t\t\tError Rates\t\t\tSubstitution :: Intended\t\t\t\t\tOmissions :: Presented\t\t\t\tInsertion :: Entered\n";
		result += "Char\tPresent\tTranscr\tEntered\tIntended\tCorrect\tNRs\tUnc\tCor\tTotal\tCount\tUnc\tCor\tNR\tTotal\tCount\tUnc\tCor\tTotal\tCount\tUnc\tCor\tTotal\n";

		for(int i = 0; i < NCHARS; i++)
		{
			String line = "";
			
			float countSubs = mSubstitutionErrors[i].getCorrCount() + mSubstitutionErrors[i].getUncCount() + mSubstitutionErrors[i].getNRCount();
			float countIns = mInsertionErrors[i].getCorrCount() + mInsertionErrors[i].getUncCount();
			float countOmi = mOmissionErrors[i].getCorrCount() + mOmissionErrors[i].getUncCount();
			
			line += getChar(i) + "\t" + df.format(mPresented[i]) + "\t" + df.format(mTranscribed[i]) + "\t" + df.format(mEntered[i]) + "\t" +
					df.format(mIntended[i]) + "\t" + df.format(mCorrect[i]) + "\t" + df.format(mNR[i]) + "\t" + df.format(mOverallErrors[i].getUnc()) + "\t" +
					df.format(mOverallErrors[i].getCorr()) + "\t" + df.format(mOverallErrors[i].getTotal()) + "\t" + 
					df.format(countSubs) + "\t" + df.format(mSubstitutionErrors[i].getUnc()) + "\t" + df.format(mSubstitutionErrors[i].getCorr()) + "\t" +
					df.format(mSubstitutionErrors[i].getNR()) + "\t" + df.format(mSubstitutionErrors[i].getTotal()) + "\t" +
					df.format(countOmi) + "\t" + df.format(mOmissionErrors[i].getUnc()) + "\t" + df.format(mOmissionErrors[i].getCorr()) + "\t" +
					df.format(mOmissionErrors[i].getTotal()) + "\t" + df.format(countIns) + "\t" + df.format(mInsertionErrors[i].getUnc()) + "\t" +
					df.format(mInsertionErrors[i].getCorr()) + "\t" + df.format(mInsertionErrors[i].getTotal());
			
			result += line + "\n";
		}
		
		result += "Total\t" + df.format(getTotal(mPresented)) + "\t" + df.format(getTotal(mTranscribed)) + "\t" + df.format(getTotal(mEntered)) + "\t" +
				df.format(getTotal(mIntended)) + "\t" + df.format(getTotal(mCorrect)) + "\t" + df.format(getTotal(mNR)) + "\t" + df.format(getTotalOverallErrors(mOverallErrors, ErrRateType.UNCORRECTED)) + "\t" +
				df.format(getTotalOverallErrors(mOverallErrors, ErrRateType.CORRECTED)) + "\t" + df.format(getTotalOverallErrors(mOverallErrors, ErrRateType.TOTAL)) + "\t" + 
				df.format(getTotalSubstitutionErrors(ErrRateType.COUNT)) + "\t" + df.format(getTotalSubstitutionErrors(ErrRateType.UNCORRECTED)) + "\t" + df.format(getTotalSubstitutionErrors(ErrRateType.CORRECTED)) + "\t" +
				df.format(getTotalSubstitutionErrors(ErrRateType.NONRECOGNITION)) + "\t" + df.format(getTotalSubstitutionErrors(ErrRateType.TOTAL)) + "\t" + 
				df.format(getTotalOmissionErrors(ErrRateType.COUNT)) + "\t" + df.format(getTotalOmissionErrors(ErrRateType.UNCORRECTED)) + "\t" + df.format(getTotalOmissionErrors(ErrRateType.CORRECTED)) + "\t" +
				df.format(getTotalOmissionErrors(ErrRateType.TOTAL)) + "\t" + df.format(getTotalInsertionErrors(ErrRateType.COUNT)) + "\t" + df.format(getTotalInsertionErrors(ErrRateType.UNCORRECTED)) + "\t" +
				df.format(getTotalInsertionErrors(ErrRateType.CORRECTED)) + "\t" + df.format(getTotalInsertionErrors(ErrRateType.TOTAL)) + "\n";
		
		return result;
	}
	
	protected String printConfusionMatrix()
	{
		String result = "";
		DecimalFormat df = new DecimalFormat("####.000");
		
		// header for character-level confusion matrix
		result += "Confusion Matrix: Wobbrock & Myers TOCHI'06\n";
		result += "Axes: X=intended, Y=produced.\n";
		result += "Cell values are number of entered characters (count), either corrected or uncorrected. To produce a % just divide Column(i) per Intended(i)";
		
		// print header
		for(int i = 0; i < mConfusionMatrixCount.length; i++)
			result += "\t" + getChar(i);
		
		result += "\n";
		
		// print lines
		for(int i = 0; i < mConfusionMatrixCount.length; i++)
		{
			result += getChar(i);
			for(int j = 0; j < mConfusionMatrixCount.length; j++)
				result += "\t" + df.format(mConfusionMatrixCount[j][i]);
			result += "\n";
		}
		
		return result;
	}
	
	public String toString()
	{
		String result = "";
		
		result += printCharacterLevelErrors();
		result += "\n";
		result += printConfusionMatrix();
		return result;
	}
	
	public String summaryToString(int ntrials)
	{
		if(ntrials == 0) return " \t \t \t \t \t ";
		
		String result = "";
		DecimalFormat df = new DecimalFormat("####.000");
		
		result += df.format(getTotalOverallErrors(mOverallErrors, ErrRateType.UNCORRECTED)) + "\t" +
				df.format(getTotalOverallErrors(mOverallErrors, ErrRateType.CORRECTED)) + "\t" + 
				df.format(getTotalOverallErrors(mOverallErrors, ErrRateType.TOTAL)) + "\t" +
				df.format(getTotalSubstitutionErrors(ErrRateType.TOTAL)) + "\t" +
				df.format(getTotalOmissionErrors(ErrRateType.TOTAL)) + "\t" + 
				df.format(getTotalInsertionErrors(ErrRateType.TOTAL));
		
		return result;
	}
	
	static public String getSummaryHeader()
	{
		return "Unc\tCorr\tTotal\tSubs\tOmi\tIns";
	}
	
	private float getTotal(float[] a)
	{
		float total = 0;
		for(int i = 0; i < a.length; i++)
			total += a[i];
		return total;
	}
	
	public float getTotalEntered() {
		return getTotal(mEntered);
	}
	
	public float getTotalTranscribed() {
		return getTotal(mTranscribed);
	}
	
	public float getInsertionErrRate()
	{
		return getTotalInsertionErrors(ErrRateType.TOTAL);
	}
	
	public float getInsertionUncErrRate()
	{
		return getTotalInsertionErrors(ErrRateType.UNCORRECTED);
	}
	
	public float getInsertionCorrErrRate()
	{
		return getTotalInsertionErrors(ErrRateType.CORRECTED);
	}
	
	@SuppressWarnings("incomplete-switch")
	private float getTotalInsertionErrors(ErrRateType type)
	{
		switch(type)
		{
		case COUNT:
			float totalCount = 0;
			for(int i = 0; i < mInsertionErrors.length; i++)
			{
				totalCount += mInsertionErrors[i].getCorrCount() + mInsertionErrors[i].getUncCount();
			}
			return totalCount;
			
		case CORRECTED:
			float totalCorr = 0;
			for(int i = 0; i < mInsertionErrors.length; i++)
			{
				totalCorr += mInsertionErrors[i].getCorrCount();
			}
			return notNaN(totalCorr / getTotal(mEntered));
			
		case UNCORRECTED: 
			float totalUnc = 0;
			for(int i = 0; i < mInsertionErrors.length; i++)
			{
				totalUnc += mInsertionErrors[i].getUncCount();
			}
			return notNaN(totalUnc / getTotal(mEntered));
			
		case TOTAL:
			totalCorr = 0;
			totalUnc = 0;
			for(int i = 0; i < mInsertionErrors.length; i++)
			{
				totalCorr += mInsertionErrors[i].getCorrCount();
				totalUnc += mInsertionErrors[i].getUncCount();
			}
			return notNaN((totalCorr + totalUnc) / getTotal(mEntered));
		}
		
		return 0f;
	}
	
	public float getOmissionErrRate()
	{
		return getTotalOmissionErrors(ErrRateType.TOTAL);
	}
	
	public float getOmissionUncErrRate()
	{
		return getTotalOmissionErrors(ErrRateType.UNCORRECTED);
	}
	
	public float getOmissionCorrErrRate()
	{
		return getTotalOmissionErrors(ErrRateType.CORRECTED);
	}
	
	@SuppressWarnings("incomplete-switch")
	private float getTotalOmissionErrors(ErrRateType type)
	{
		switch(type)
		{
		case COUNT:
			float totalCount = 0;
			for(int i = 0; i < mOmissionErrors.length; i++)
			{
				totalCount += mOmissionErrors[i].getCorrCount() + mOmissionErrors[i].getUncCount();
			}
			return totalCount;
			
		case CORRECTED:
			float totalCorr = 0;
			for(int i = 0; i < mOmissionErrors.length; i++)
			{
				totalCorr += mOmissionErrors[i].getCorrCount();
			}
			return notNaN(totalCorr / getTotal(mPresented));
			
		case UNCORRECTED: 
			float totalUnc = 0;
			for(int i = 0; i < mOmissionErrors.length; i++)
			{
				totalUnc += mOmissionErrors[i].getUncCount();
			}
			return notNaN(totalUnc / getTotal(mPresented));
			
		case TOTAL:
			totalCorr = 0;
			totalUnc = 0;
			for(int i = 0; i < mOmissionErrors.length; i++)
			{
				totalCorr += mOmissionErrors[i].getCorrCount();
				totalUnc += mOmissionErrors[i].getUncCount();
			}
			return notNaN((totalCorr + totalUnc) / getTotal(mPresented));
		}
		return 0f;
	}
	
	public float getSubstitutionErrRate()
	{
		return getTotalSubstitutionErrors(ErrRateType.TOTAL);
	}
	
	public float getSubstitutionCorrErrRate()
	{
		return getTotalSubstitutionErrors(ErrRateType.CORRECTED);
	}
	
	public float getSubstitutionUncErrRate()
	{
		return getTotalSubstitutionErrors(ErrRateType.UNCORRECTED);
	}
	
	private float getTotalSubstitutionErrors(ErrRateType type)
	{
		switch(type)
		{
		case COUNT:
			float totalCount = 0;
			for(int i = 0; i < mSubstitutionErrors.length; i++)
			{
				totalCount += mSubstitutionErrors[i].getCorrCount() + mSubstitutionErrors[i].getUncCount() + mSubstitutionErrors[i].getNRCount();
			}
			return totalCount;
			
		case CORRECTED:
			float totalCorr = 0;
			for(int i = 0; i < mSubstitutionErrors.length; i++)
			{
				totalCorr += mSubstitutionErrors[i].getCorrCount();
			}
			return notNaN(totalCorr / getTotal(mIntended));
			
		case UNCORRECTED: 
			float totalUnc = 0;
			for(int i = 0; i < mSubstitutionErrors.length; i++)
			{
				totalUnc += mSubstitutionErrors[i].getUncCount();
			}
			return notNaN(totalUnc / getTotal(mIntended));
			
		case NONRECOGNITION:
			float totalNR = 0;
			for(int i = 0; i < mSubstitutionErrors.length; i++)
			{
				totalNR += mSubstitutionErrors[i].getNRCount();
			}
			return notNaN(totalNR / getTotal(mIntended));
			
		case TOTAL:
			totalCorr = 0;
			totalUnc = 0;
			totalNR = 0;
			for(int i = 0; i < mSubstitutionErrors.length; i++)
			{
				totalCorr += mSubstitutionErrors[i].getCorrCount();
				totalUnc += mSubstitutionErrors[i].getUncCount();
				totalNR += mSubstitutionErrors[i].getNRCount();
			}
			return notNaN((totalCorr + totalUnc + totalNR) / getTotal(mIntended));
		}
		
		return 0f;
	}
	
	public float getUncErrRate()
	{
		return getTotalOverallErrors(mOverallErrors, ErrRateType.UNCORRECTED);
	}
	
	public float getCorrErrRate()
	{
		return getTotalOverallErrors(mOverallErrors, ErrRateType.CORRECTED);
	}
	
	public float getTotalErrRate()
	{
		return getTotalOverallErrors(mOverallErrors, ErrRateType.TOTAL);
	}
	
	@SuppressWarnings("incomplete-switch")
	private float getTotalOverallErrors(ErrorRateResults[] a, ErrRateType type)
	{		
		switch(type)
		{
		case CORRECTED:
			float corrSum = 0;
			for(int i = 0; i < a.length; i++)
			{
				corrSum += mNoErrors[i].getCorrCount();
			}
			return 1 - notNaN((corrSum / (getTotal(mEntered) - getTotal(mTranscribed))));
			
		case UNCORRECTED: 
			float uncSum = 0;
			for(int i = 0; i < a.length; i++)
			{
				uncSum += mNoErrors[i].getUncCount();
			}
			return notNaN(1 - (uncSum / getTotal(mTranscribed)));
			
		case TOTAL: 
			corrSum = 0;
			uncSum = 0;
			for(int i = 0; i < a.length; i++)
			{
				corrSum += mNoErrors[i].getCorrCount();
				uncSum += mNoErrors[i].getUncCount();
			}
			return notNaN(1 - ((uncSum + corrSum) / getTotal(mEntered)));
		}
		
		return 0f;
	}

	public float getTotalCorrections() {
		float corrSum = 0;
		for(int i = 0; i < mOverallErrors.length; i++)
		{
			corrSum += mNoErrors[i].getCorrCount();
		}
		return corrSum;
	}
	
	/**
	 * Getters
	 */
	
	public float[] getPresented() { return mPresented; }
	public void setPresented(float[] presented) { mPresented = presented; }
	
	public float[] getTranscribed() { return mTranscribed; }
	public void setTranscribed(float[] transcribed) { mTranscribed = transcribed; }
	
	public float[] getEntered() { return mEntered; }
	public void setEntered(float[] entered) { mEntered = entered; }
	
	public float[] getIntended() { return mIntended; }
	public void setIntended(float[] intended) { mIntended = intended; }
	
	public float[] getCorrect() { return mCorrect; }
	public void setCorrect(float[] correct) { mCorrect = correct; }
	
	public float[] getNR() { return mNR; }
	public void setNR(float[] nr) { mNR = nr; }
	
	public float[][] getConfusionMatrix() { return mConfusionMatrixCount; }
	public void setConfusionMatrix(float[][] matrix) { mConfusionMatrixCount = matrix; }
	
	public ErrorRateResults[] getSubstitutions() { return mSubstitutionErrors; }
	public ErrorRateResults[] getOmissions() { return mOmissionErrors; }
	public ErrorRateResults[] getInsertions() { return mInsertionErrors; }
	public ErrorRateResults[] getNoErrors() { return mNoErrors; }
	
	public void setMSD(TextEntryMSD msd) { mMSD = msd; }
	
	public TextEntryMSD getMSD() { return mMSD; }
	
	public void setStreamAlignments(ArrayList<StreamAlignment> alignments) { mStreamAlignments = alignments; }
	
	public ArrayList<StreamAlignment> getStreamAlignments() { return mStreamAlignments; }
}
