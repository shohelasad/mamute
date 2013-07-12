package br.com.caelum.brutal.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import br.com.caelum.brutal.dao.WithUserDAO.OrderType;
import br.com.caelum.brutal.dao.WithUserDAO.UserRole;
import br.com.caelum.brutal.model.News;
import br.com.caelum.brutal.model.User;
import br.com.caelum.vraptor.ioc.Component;
@Component
public class NewsDAO implements PaginatableDAO  {
    private static final int SPAM_BOUNDARY = -5;
	private final WithUserDAO<News> withAuthor;
	private Session session;
	private InvisibleForUsersRule invisible;
    
    public NewsDAO(Session session, InvisibleForUsersRule invisible) {
        this.session = session;
		this.invisible = invisible;
		this.withAuthor = new WithUserDAO<News>(session, News.class, UserRole.AUTHOR);
    }
    
	public List<News> allVisible(Integer initPage, Integer pageSize) {
		String hql = "from News as n join fetch n.information ni" +
				" join fetch n.author na" +
				" join fetch n.lastTouchedBy na" +
				" "+ invisibleFilter("and") +" " + spamFilter() +" order by n.lastUpdatedAt desc";
		Query query = session.createQuery(hql);
		return query.setMaxResults(pageSize)
			.setFirstResult(firstResultOf(initPage, pageSize))
			.list();
	}

	private int firstResultOf(Integer initPage, Integer pageSize) {
		return pageSize * (initPage-1);
	}

	private String spamFilter() {
		return "n.voteCount > "+SPAM_BOUNDARY;
	}

	private String invisibleFilter(String connective) {
		return invisible.getInvisibleOrNotFilter("n", connective);
	}

	public List<News> postsToPaginateBy(User user, OrderType orderByWhat, Integer page) {
		return withAuthor.by(user,orderByWhat, page);
	}
	
	@Override
	public Long countWithAuthor(User author) {
		return withAuthor.count(author);
	}

	@Override
	public Long numberOfPagesTo(User author) {
		return withAuthor.numberOfPagesTo(author);
	}

	public void save(News news) {
		session.save(news);
	}

	public long numberOfPages(Integer pageSize) {
		String hql = "select count(*) from News n " + invisibleFilter("and") + " " + spamFilter();
		Long totalItems = (Long) session.createQuery(hql).uniqueResult();
		long result = calculatePages(totalItems, pageSize);
		return result;
	}
	
	private long calculatePages(Long count, Integer pageSize) {
		long result = count/pageSize.longValue();
		if (count % pageSize.longValue() != 0) {
			result++;
		}
		return result;
	}
}