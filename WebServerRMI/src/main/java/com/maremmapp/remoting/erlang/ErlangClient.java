package com.maremmapp.remoting.erlang;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangDecodeException;
import com.ericsson.otp.erlang.OtpErlangExit;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.ericsson.otp.erlang.OtpMbox;
import com.ericsson.otp.erlang.OtpNode;
import com.maremmapp.Utils;
import com.maremmapp.remoting.entity.Feedback;

/*
 * Represents a local OtpNode. This class is used when you do not wish to manage connections yourself - outgoing connections 
 * are established as needed, and incoming connections accepted automatically.
 * This class supports the use of a mailbox API for communication, while management of the underlying communication mechanism 
 * is automatic and hidden from the application programmer.
 * Once an instance of this class has been created, obtain one or more mailboxes in order to send or receive messages. 
 * The first message sent to a given node will cause a connection to be set up to that node. 
 * Any messages received will be delivered to the appropriate mailboxes.
 */

@Component
public class ErlangClient {
	@Autowired
	Utils utils;
	
	private static OtpNode clientNode;
	private OtpMbox mbox;
	
	// init is used in order to initialize clientNode and mbox. 
	// The class constructor is not used since utils attributes are null
	@PostConstruct
	public void init() {
		try {
			clientNode = new OtpNode(utils.getClientNodeName());
	        System.out.println("utils: "+utils.getClientNodeName()+ " ,cookie "+ utils.getCookie());
	        mbox = clientNode.createMbox(utils.getMBoxName());
	        System.out.println("Created mailbox "+ mbox.getName());
		}catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean insertFeedback(Feedback ev) {
		System.out.println("[DEBUG] insertFeedback...");
		OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{
        		new OtpErlangAtom("$gen_call"),
        		new OtpErlangTuple(new OtpErlangObject[] {
                        this.mbox.self(),
                        new OtpErlangAtom(utils.getClientNodeName())
                }),
        		new OtpErlangTuple(new OtpErlangObject[] {
                        new OtpErlangAtom("insert_feedback"),
                        new OtpErlangString(ev.getOwner()),
                        new OtpErlangString(ev.getMessage())
                })
        });

        //sending out the request
        mbox.send(utils.getServerRegisteredName(), utils.getServerNodeName(), reqMsg);
        //blocking receive operation
        OtpErlangTuple msg;
		try {
			msg = (OtpErlangTuple)mbox.receive();
		} catch (OtpErlangExit e) {
			e.printStackTrace();
			return false;
		} catch (OtpErlangDecodeException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//Function used to cast feedbacks received from erlang node (which are in tuple type) in the relative java Feedback object
	private Feedback castToFeedback(OtpErlangTuple erlTuple) {
		Feedback feedback = new Feedback();
		feedback.setId(((OtpErlangLong)erlTuple.elementAt(0)).longValue());
		OtpErlangTuple date = ((OtpErlangTuple) erlTuple.elementAt(1));
		feedback.setOwner(((OtpErlangString)erlTuple.elementAt(2)).stringValue());
		feedback.setMessage(((OtpErlangString)erlTuple.elementAt(3)).stringValue());
		
		String dateTime = "";
    	for (int i = 0; i <= 2; i++) {
    		String element = date.elementAt(i).toString();
    		if (element.length() == 1)
    			dateTime += "0" + element;
    		else
    			dateTime += element;
    		if (i != 2)
    			dateTime += "-";
        }
    	//Create a LocalDate item from a String and set feedback date field with its value
    	LocalDate lDate = LocalDate.parse(dateTime);
    	feedback.setDate(lDate);
		
		return feedback;		
	}
	
	public List<Feedback> getAllFeedbacks() {
		OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{
        		new OtpErlangAtom("$gen_call"),
        		new OtpErlangTuple(new OtpErlangObject[] {
                        this.mbox.self(),
                        new OtpErlangAtom(utils.getClientNodeName())
                }),
        		new OtpErlangTuple(new OtpErlangObject[] {
                        new OtpErlangAtom("get_all_feedback")
                })
        });
        //sending out the request
		mbox.send(utils.getServerRegisteredName(), utils.getServerNodeName(), reqMsg);
        //blocking receive operation
        OtpErlangTuple msg;
        List<Feedback> result = null;
		try {
			msg = (OtpErlangTuple)mbox.receive();
	        if( msg.elementAt(1) instanceof OtpErlangList) {
	        	OtpErlangList resultList = (OtpErlangList) msg.elementAt(1);
	        	result = new ArrayList<Feedback>();
	        	for( OtpErlangObject oeo : resultList) {
	        		Feedback fb = castToFeedback((OtpErlangTuple)oeo);
	        		result.add(fb);
	        	}
	        }
		} catch (OtpErlangExit e) {
			e.printStackTrace();
			return null;
		} catch (OtpErlangDecodeException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	public List<Feedback> getUserFeedbacks(String username) {
		OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{
        		new OtpErlangAtom("$gen_call"),
        		new OtpErlangTuple(new OtpErlangObject[] {
                        this.mbox.self(),
                        new OtpErlangAtom(utils.getClientNodeName())
                }),
        		new OtpErlangTuple(new OtpErlangObject[] {
                        new OtpErlangAtom("get_feedback"),
                        new OtpErlangString(username)
                })
        });
        //sending out the request
        mbox.send(utils.getServerRegisteredName(), utils.getServerNodeName(), reqMsg);
        //blocking receive operation
        OtpErlangTuple msg;
        List<Feedback> result = null;
		try {
			msg = (OtpErlangTuple)mbox.receive();
	        if( msg.elementAt(1) instanceof OtpErlangList) {
	        	OtpErlangList resultList = (OtpErlangList) msg.elementAt(1);
	        	result = new ArrayList<Feedback>();
	        	for( OtpErlangObject oeo : resultList) {
	        		Feedback fb = castToFeedback((OtpErlangTuple)oeo);
	        		result.add(fb);
	        	}
	        }
		} catch (OtpErlangExit e) {
			e.printStackTrace();
			return null;
		} catch (OtpErlangDecodeException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	public boolean deleteFeedback(long id) {
		System.out.println("[DEBUG] Deleting feedback with id: " + id);
		OtpErlangTuple reqMsg = new OtpErlangTuple(new OtpErlangObject[]{
        		new OtpErlangAtom("$gen_call"),
        		new OtpErlangTuple(new OtpErlangObject[] {
                        this.mbox.self(),
                        new OtpErlangAtom(utils.getClientNodeName())
                }),
        		new OtpErlangTuple(new OtpErlangObject[] {
        				new OtpErlangAtom("delete_feedback"),
                        new OtpErlangLong(id)
                })
        });
		//sending out the request
		OtpErlangTuple msg;
		mbox.send(utils.getServerRegisteredName(), utils.getServerNodeName(), reqMsg);
        try {
            msg = (OtpErlangTuple) mbox.receive();
            String result = ((OtpErlangAtom) msg.elementAt(1)).atomValue();
            return result.compareTo("success") == 0;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }
}