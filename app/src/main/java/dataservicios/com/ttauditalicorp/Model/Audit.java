package dataservicios.com.ttauditalicorp.Model;

/**
 * Created by Jaime Eduardo on 05/10/2015.
 */
public class Audit {

    private int id;
    private String name;
    private int store_id;
    private int score;

    /**
     * Return id the audit
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Set id the audit
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get name the audit
     *
     * @return name
     */

    public String getName() {
        return name;
    }

    /**
     * Set name the audit
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get store id the audit
     *
     * @return store_id
     */
    public int getStore_id() {
        return store_id;
    }

    /**
     * Set store id the audit
     *
     * @param store_id
     */
    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }


    /**
     * Get Score the auditory
     * @return score
     */
    public int getScore() {
        return score;
    }

    /**
     * Set Score the auditory
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }
}
