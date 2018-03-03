package synapticloop.sample.h2zero.sqlite3.model;

// - - - - thoughtfully generated by synapticloop h2zero - - - - 
//    with the use of synapticloop templar templating language
//                  (java-create-model.templar)

import java.util.HashMap;

import java.util.Map;

import synapticloop.sample.h2zero.sqlite3.model.util.Constants;


public class UserTitle {
	// the binder is unused in code, but will generate compile problems if this 
	// class is no longer referenced in the h2zero file. Just a nicety for
	// removing dead code
	@SuppressWarnings("unused")
	private static final String BINDER = Constants.USER_TITLE_BINDER;

	public static final UserTitle MR = new UserTitle(new Long(1), "Mr.", new Integer(1));
	public static final UserTitle MRS = new UserTitle(new Long(2), "Mrs.", new Integer(2));
	public static final UserTitle MISS = new UserTitle(new Long(3), "Miss", new Integer(3));
	public static final UserTitle DR = new UserTitle(new Long(4), "Dr.", new Integer(4));

	public static final UserTitle[] ALL =  {
		UserTitle.MR, UserTitle.MRS, UserTitle.MISS, UserTitle.DR
	};

	public static final Map<Long, UserTitle> ALL_LOOKUP = new HashMap<Long, UserTitle>();
	static{
		ALL_LOOKUP.put(new Long(1), UserTitle.MR);
		ALL_LOOKUP.put(new Long(2), UserTitle.MRS);
		ALL_LOOKUP.put(new Long(3), UserTitle.MISS);
		ALL_LOOKUP.put(new Long(4), UserTitle.DR);

	};

	public static final String PRIMARY_KEY_FIELD = "id_user_title";


	private Long idUserTitle = null;
	private String nmUserTitle = null;
	private Integer numOrderBy = null;

	public UserTitle(Long idUserTitle, String nmUserTitle, Integer numOrderBy) {
		this.idUserTitle = idUserTitle;
		this.nmUserTitle = nmUserTitle;
		this.numOrderBy = numOrderBy;
	}
	/*
	 * Boring ol' getters and setters 
	 */

	public Long getPrimaryKey() { return(this.idUserTitle); }
	public Long getIdUserTitle() { return(this.idUserTitle); }
	public String getNmUserTitle() { return(this.nmUserTitle); }
	public Integer getNumOrderBy() { return(this.numOrderBy); }

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Model[UserTitle]\n");
		stringBuilder.append("  Field[idUserTitle:" + this.idUserTitle + "]\n");
		stringBuilder.append("  Field[nmUserTitle:" + this.nmUserTitle + "]\n");
		stringBuilder.append("  Field[numOrderBy:" + this.numOrderBy + "]\n");
		return(stringBuilder.toString());
	}

	public String toJsonString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\n");
		stringBuilder.append("  \"type\": \"UserTitle\",\n");
		stringBuilder.append("  \"idUserTitle\": " + this.idUserTitle + " , \n");
		stringBuilder.append("  \"nmUserTitle\": \"" + this.nmUserTitle + "\" , \n");
		stringBuilder.append("  \"numOrderBy\": " + this.numOrderBy + " \n");
		stringBuilder.append("}\n");
		return(stringBuilder.toString());
	}

	public String getJsonString() {
		return(toJsonString());
	}
}