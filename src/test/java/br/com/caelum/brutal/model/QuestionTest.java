package br.com.caelum.brutal.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.caelum.brutal.integracao.dao.TestCase;

public class QuestionTest  extends TestCase{

	@Test(expected = RuntimeException.class)
	public void can_not_be_marked_as_solved_by_the_an_answer_that_is_not_mine() {
		Question shouldILiveForever = question("", "", null);
		Answer yes = answer("", null, null);
		shouldILiveForever.markAsSolvedBy(yes);
	}

	@Test
	public void can_be_marked_as_solved_by_the_an_answer_that_is_mine() {
		Question shouldILiveForever = question("", "", null);
		Answer yes = answer("my answer", shouldILiveForever, null);
		
		shouldILiveForever.markAsSolvedBy(yes);
		
		assertEquals(yes, shouldILiveForever.getSolution());
	}
	
	@Test
	public void should_be_touched_when_marked_as_solved() {
		Question shouldILiveForever = question("", "", null);
		User leo = new User("", "", "");
		Answer yes = answer("my answer", shouldILiveForever, leo);
		
		assertEquals(User.GHOST, shouldILiveForever.getLastTouchedBy());

		shouldILiveForever.markAsSolvedBy(yes);
		
		assertEquals(leo, shouldILiveForever.getLastTouchedBy());
	}

	@Test
	public void should_remove_vote_values_and_update_vote_count() {
		Question question = question("", "", null);
		assertEquals(0l, question.getVoteCount());
		Vote firstVote = new Vote(null, VoteType.UP);
		question.substitute(null, firstVote);
		assertEquals(1l, question.getVoteCount());
		question.substitute(firstVote, new Vote(null, VoteType.DOWN));
		assertEquals(-1l, question.getVoteCount());
		question.substitute(null, new Vote(null, VoteType.DOWN));
		assertEquals(-2l, question.getVoteCount());
	}

}
