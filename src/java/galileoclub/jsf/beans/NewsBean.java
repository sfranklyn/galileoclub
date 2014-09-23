/*
 * NewsBean.java
 * 
 * Created on Feb 2, 2009, 1:45:12 PM
 */
package galileoclub.jsf.beans;

import galileoclub.ejb.datamodel.NewsDataModelBean;
import galileoclub.ejb.datamodel.NewsDataModelRemote;
import galileoclub.ejb.service.NewsServiceRemote;
import galileoclub.jpa.News;
import galileoclub.jpa.Users;
import galileoclub.jsf.model.DatabaseDataModel;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *
 * @author Samuel Franklyn
 */
public class NewsBean {

    private final Integer noOfRows = 30;
    private final Integer fastStep = 10;
    private final DatabaseDataModel dataModel = new DatabaseDataModel();
    private News news = null;
    private PropertyResourceBundle messageSource = null;
    private VisitBean visit = null;
    @EJB
    private NewsDataModelRemote newsDataModelRemote;
    @EJB
    private NewsServiceRemote newsServiceRemote;

    public String create() {
        news = new News();
        news.setNewsDate(new Date());
        Users users = new Users();
        users.setUserId(visit.getUserId());
        news.setUserId(users);
        return "redirect:secure/newsCreate";
    }

    public String saveCreate() {
        String result = "secure/newsCreate";

        List<String> errorList = newsServiceRemote.saveCreate(news, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/news";
        }

        return result;
    }

    public String read() {
        news = (News) dataModel.getRowData();
        return "redirect:secure/newsRead";
    }

    public String update() {
        news = (News) dataModel.getRowData();
        return "redirect:secure/newsUpdate";
    }

    public String saveUpdate() {
        String result = "secure/newsUpdate";

        List<String> errorList = newsServiceRemote.saveUpdate(news, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/news";
        }

        return result;
    }

    public String delete() {
        news = (News) dataModel.getRowData();
        return "redirect:secure/newsDelete";
    }

    public String saveDelete() {
        String result = "secure/newsDelete";

        List<String> errorList = newsServiceRemote.saveDelete(news, visit.getLocale());
        if (errorList.size() > 0) {
            for (String error : errorList) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        error, ""));
            }
        } else {
            result = "redirect:secure/news";
        }

        return result;
    }

    public Integer getNoOfRows() {
        return noOfRows;
    }

    public Integer getFastStep() {
        return fastStep;
    }

    public DatabaseDataModel getDataModel() {
        dataModel.setSelect(NewsDataModelBean.SELECT_ALL);
        dataModel.setSelectCount(NewsDataModelBean.SELECT_ALL_COUNT);
        dataModel.setSelectParam(null);
        dataModel.setWrappedData(newsDataModelRemote);
        return dataModel;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public PropertyResourceBundle getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(PropertyResourceBundle messageSource) {
        this.messageSource = messageSource;
    }

    public VisitBean getVisit() {
        return visit;
    }

    public void setVisit(VisitBean visit) {
        this.visit = visit;
    }

    public NewsDataModelRemote getNewsDataModelRemote() {
        return newsDataModelRemote;
    }

    public void setNewsDataModelRemote(NewsDataModelRemote newsDataModelRemote) {
        this.newsDataModelRemote = newsDataModelRemote;
    }

    public NewsServiceRemote getNewsServiceRemote() {
        return newsServiceRemote;
    }

    public void setNewsServiceRemote(NewsServiceRemote newsServiceRemote) {
        this.newsServiceRemote = newsServiceRemote;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
