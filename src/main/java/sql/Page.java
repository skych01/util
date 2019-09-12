package sql;

import java.util.List;


public class Page<T> {
    //数据
    private List<T> content;
    //当前页数
    private int number;
    //每页记录数
    private  int size;
    //总记录数
    private long totalElements;
    //总页数
    private long totalPages;
    //上一页
    private boolean last;
    //下一页
    private boolean first;


    /**
     * 构造Page对象
     * @param number 当前页
     * @param size  每页记录数
     * @param totalElements 总记录数
     * @param content  数据
     */
    public Page(int number, int size, long totalElements , List<T> content) {
        this.number=number;
        this.size=size;
        this.totalElements=totalElements;
        this.content=content;
        long totalPages =totalElements / size;

        if (totalElements % size != 0) {
            totalPages++;
        }
        this.totalPages=totalPages;
        this.first = (number==1) ;
        this.last = (number==totalPages) ;
    }

    public Page() {
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

}
