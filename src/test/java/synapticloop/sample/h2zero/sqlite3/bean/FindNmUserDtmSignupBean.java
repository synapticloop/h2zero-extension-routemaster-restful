package synapticloop.sample.h2zero.sqlite3.bean;

// - - - - thoughtfully generated by synapticloop h2zero - - - - 
//    with the use of synapticloop templar templating language
//           (java-create-select-clause-bean.templar)

import java.sql.Date;
import java.sql.Timestamp;

public class FindNmUserDtmSignupBean {
	private String nmUser = null;
	private Timestamp dtmSignup = null;

	public FindNmUserDtmSignupBean(String nmUser, Timestamp dtmSignup) {
		this.nmUser = nmUser;
		this.dtmSignup = dtmSignup;
	}

	public String getNmUser() { return(this.nmUser); }
	public void setNmUser(String nmUser) { this.nmUser = nmUser; }
	public Timestamp getDtmSignup() { return(this.dtmSignup); }
	public void setDtmSignup(Timestamp dtmSignup) { this.dtmSignup = dtmSignup; }
}