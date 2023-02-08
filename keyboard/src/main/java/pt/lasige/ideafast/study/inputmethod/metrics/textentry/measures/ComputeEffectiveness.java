package pt.lasige.ideafast.study.inputmethod.metrics.textentry.measures;

import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.EffectivenessResults;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.TextEntryCondition;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.TextEntryMSD;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.TextEntryParticipantSession;
import pt.lasige.ideafast.study.inputmethod.metrics.textentry.datastructures.TextEntryTrial;

public class ComputeEffectiveness {
	
	static public TextEntryCondition computeConditionEffectivenessResults(TextEntryCondition condition)
	{
		EffectivenessResults effectivenessResults = condition.getConditionResults().getEffectivenessResults();
		
		// sum all values
		int counter = 0;
		for(TextEntryParticipantSession session : condition.getSessions())
		{
			if(session.getTrials().size() == 0) continue;
			EffectivenessResults sessionresults = session.getSessionResults().getEffectivenessResults();
			
			effectivenessResults.setCorrect(effectivenessResults.getCorrect() + sessionresults.getCorrect());
			effectivenessResults.setIncorrectNotFixed(effectivenessResults.getIncorrectNotFixed() + sessionresults.getIncorrectNotFixed());
			effectivenessResults.setIncorrectButFixed(effectivenessResults.getIncorrectButFixed() + sessionresults.getIncorrectButFixed());
			effectivenessResults.setFixes(effectivenessResults.getFixes() + sessionresults.getFixes());
			effectivenessResults.setKSPC(effectivenessResults.getKSPC() + sessionresults.getKSPC());
			effectivenessResults.setMSDErrorRate(effectivenessResults.getMSDErrorRate() + sessionresults.getMSDErrorRate());
			effectivenessResults.setTotalErrorRate(effectivenessResults.getTotalErrorRate() + sessionresults.getTotalErrorRate());
			effectivenessResults.setUncorrectedErrorRate(effectivenessResults.getUncorrectedErrorRate() + sessionresults.getUncorrectedErrorRate());
			effectivenessResults.setCorrectedErrorRate(effectivenessResults.getCorrectedErrorRate() + sessionresults.getCorrectedErrorRate());
			counter++;
		}
		
		// calculate averages
		int size = counter;
		//effectivenessResults.setCorrect(effectivenessResults.getCorrect() / size);
		//effectivenessResults.setIncorrectNotFixed(effectivenessResults.getIncorrectNotFixed() / size);
		//effectivenessResults.setIncorrectButFixed(effectivenessResults.getIncorrectButFixed() / size);
		//effectivenessResults.setFixes(effectivenessResults.getFixes() / size);
		effectivenessResults.setKSPC(effectivenessResults.getKSPC() / size);
		effectivenessResults.setMSDErrorRate(effectivenessResults.getMSDErrorRate() / size);
		effectivenessResults.setTotalErrorRate(effectivenessResults.getTotalErrorRate() / size);
		effectivenessResults.setUncorrectedErrorRate(effectivenessResults.getUncorrectedErrorRate() / size);
		effectivenessResults.setCorrectedErrorRate(effectivenessResults.getCorrectedErrorRate() / size);
		
		return condition;
	}
	
	static public TextEntryParticipantSession computeSessionEffectivenessResults(TextEntryParticipantSession session)
	{
		EffectivenessResults effectivenessResults = session.getSessionResults().getEffectivenessResults();
		
		int counter = 0;
		// sum all values
		for(TextEntryTrial trial : session.getTrials())
		{
			if(!trial.getTrialResults().COMPUTE_ERRORS) continue;
			
			EffectivenessResults trialResults = trial.getTrialResults().getEffectivenessResults();
			counter++;
			
			effectivenessResults.setCorrect(effectivenessResults.getCorrect() + trialResults.getCorrect());
			effectivenessResults.setIncorrectNotFixed(effectivenessResults.getIncorrectNotFixed() + trialResults.getIncorrectNotFixed());
			effectivenessResults.setIncorrectButFixed(effectivenessResults.getIncorrectButFixed() + trialResults.getIncorrectButFixed());
			effectivenessResults.setFixes(effectivenessResults.getFixes() + trialResults.getFixes());
			effectivenessResults.setKSPC(effectivenessResults.getKSPC() + trialResults.getKSPC());
			effectivenessResults.setMSDErrorRate(effectivenessResults.getMSDErrorRate() + trialResults.getMSDErrorRate());
			effectivenessResults.setTotalErrorRate(effectivenessResults.getTotalErrorRate() + trialResults.getTotalErrorRate());
			effectivenessResults.setUncorrectedErrorRate(effectivenessResults.getUncorrectedErrorRate() + trialResults.getUncorrectedErrorRate());
			effectivenessResults.setCorrectedErrorRate(effectivenessResults.getCorrectedErrorRate() + trialResults.getCorrectedErrorRate());
		}
		
		// calculate averages
		int size = counter;//session.getTrials().size();
		//effectivenessResults.setCorrect(effectivenessResults.getCorrect() / size);
		//effectivenessResults.setIncorrectNotFixed(effectivenessResults.getIncorrectNotFixed() / size);
		//effectivenessResults.setIncorrectButFixed(effectivenessResults.getIncorrectButFixed() / size);
		//effectivenessResults.setFixes(effectivenessResults.getFixes() / size);
		effectivenessResults.setKSPC(effectivenessResults.getKSPC() / size);
		effectivenessResults.setMSDErrorRate(effectivenessResults.getMSDErrorRate() / size);
		effectivenessResults.setTotalErrorRate(effectivenessResults.getTotalErrorRate() / size);
		effectivenessResults.setUncorrectedErrorRate(effectivenessResults.getUncorrectedErrorRate() / size);
		effectivenessResults.setCorrectedErrorRate(effectivenessResults.getCorrectedErrorRate() / size);
		
		return session;
	}
	
	static public TextEntryTrial computeTrialEffectivenessResults(TextEntryTrial trial)
	{
		if(!trial.getTrialResults().COMPUTE_ERRORS) return trial;

		// INF = MSD
		TextEntryMSD msd = ComputeCharacterLevel.computeMSD(trial.getRequiredSentence(), trial.getTranscribedSentence());
		float inf = msd.getMSD();
		
		// C
		float c = msd.getCorrect();

		// F = number of backspaces
		// count <<
		String aux = new String(trial.getInputStream());
		float f = trial.getInputStream().length() - aux.replace("<<", "<").length();
		
		// IF = inputstream - transcribed - F
		float ibf = trial.getInputStream().length() - (f*2) - trial.getTranscribedSentence().length(); // x2 because backspace uses two characters
		
		EffectivenessResults effectivenessresults = new EffectivenessResults(c, inf, f, ibf);
		trial.getTrialResults().setEffectivenessResults(effectivenessresults);

		// error rate measures
		effectivenessresults.setMSDErrorRate(inf / (c + inf) * 100);
		effectivenessresults.setKSPC((c + inf + ibf + f) / (c + inf));
		effectivenessresults.setTotalErrorRate((inf + ibf) / (c + inf + ibf) * 100);
		effectivenessresults.setUncorrectedErrorRate(inf / (c + inf + ibf) * 100);
		effectivenessresults.setCorrectedErrorRate(ibf / (c + inf + ibf) * 100);
		
		return trial;
	}
	
}
