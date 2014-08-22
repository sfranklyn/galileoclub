/*
 * DatabaseDataModel.java
 *
 * Created on August 6, 2007, 1:27 PM
 *
 */
package galileoclub.jsf.model;

import galileoclub.ejb.datamodel.DataModelDao;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

/**
 *
 * @author Samuel Franklyn
 */
public class DatabaseDataModel extends DataModel {

    private static final Logger log = Logger.getLogger(DatabaseDataModel.class.getName());
    private DataModelDao dataModel = null;
    private AtomicInteger rowIndex = new AtomicInteger(-1);
    private String select = null;
    private String selectCount = null;
    private Map selectParam = null;

    @Override
    @SuppressWarnings("unchecked")
    public int getRowCount() {
        if (getDataModel() == null) {
            return -1;
        }
        try {
            return Long.valueOf(getDataModel().getAllCount(selectCount, selectParam)).intValue();
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isRowAvailable() {
        if ((getDataModel() == null) || rowIndex.get() < 0) {
            return false;
        }
        if (rowIndex.get() > (getRowCount() - 1)) {
            return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getRowData() {
        if (getDataModel() == null) {
            return null;
        }
        if (!isRowAvailable()) {
            final String errMsg = "Row is unavalaible";
            throw new IllegalArgumentException(errMsg);
        }
        final List list;
        try {
            list = getDataModel().getAll(select, selectParam, rowIndex.get(), 1);
        } catch (Exception ex) {
            log.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public int getRowIndex() {
        return rowIndex.get();
    }

    @Override
    public void setRowIndex(final int rowIndex) {
        if (rowIndex < -1) {
            final String errMsg = "Illegal rowIndex " + rowIndex;
            throw new IllegalArgumentException(errMsg);
        }
        final int oldRowIndex = this.rowIndex.get();
        this.rowIndex.set(rowIndex);
        final DataModelListener[] listeners = getDataModelListeners();
        if ((listeners != null) && (listeners.length > 0)) {
            if ((getDataModel() != null) && (oldRowIndex != this.rowIndex.get())) {
                Object data = null;
                if (isRowAvailable()) {
                    data = getRowData();
                }
                final DataModelEvent event = new DataModelEvent(this, this.rowIndex.get(), data);
                for (int i = 0; i < listeners.length; i++) {
                    listeners[i].rowSelected(event);
                }
            }
        }
    }

    @Override
    public Object getWrappedData() {
        return getDataModel();
    }

    @Override
    public void setWrappedData(final Object object) {
        setDataModel((DataModelDao) object);
        if (getDataModel() == null) {
            setRowIndex(-1);
        } else {
            setRowIndex(0);
        }

    }

    public DataModelDao getDataModel() {
        return dataModel;
    }

    public void setDataModel(final DataModelDao value) {
        this.dataModel = value;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(String selectCount) {
        this.selectCount = selectCount;
    }

    public Map getSelectParam() {
        return selectParam;
    }

    public void setSelectParam(Map selectParam) {
        this.selectParam = selectParam;
    }
}
