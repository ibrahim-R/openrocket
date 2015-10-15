package net.sf.openrocket.file.openrocket.importt;

import java.util.HashMap;

import org.xml.sax.SAXException;

import net.sf.openrocket.aerodynamics.Warning;
import net.sf.openrocket.aerodynamics.WarningSet;
import net.sf.openrocket.file.DocumentLoadingContext;
import net.sf.openrocket.file.simplesax.AbstractElementHandler;
import net.sf.openrocket.file.simplesax.ElementHandler;
import net.sf.openrocket.file.simplesax.PlainTextHandler;
import net.sf.openrocket.motor.Motor;
import net.sf.openrocket.motor.MotorInstance;
import net.sf.openrocket.rocketcomponent.FlightConfigurationID;
import net.sf.openrocket.rocketcomponent.IgnitionEvent;
import net.sf.openrocket.rocketcomponent.MotorMount;
import net.sf.openrocket.rocketcomponent.Rocket;
import net.sf.openrocket.rocketcomponent.RocketComponent;

class MotorMountHandler extends AbstractElementHandler {
	private final DocumentLoadingContext context;
	private final MotorMount mount;
	private MotorHandler motorHandler;
	private IgnitionConfigurationHandler ignitionConfigHandler;
	
	public MotorMountHandler(MotorMount mount, DocumentLoadingContext context) {
		this.mount = mount;
		this.context = context;
		mount.setMotorMount(true);
	}
	
	@Override
	public ElementHandler openElement(String element, HashMap<String, String> attributes,
			WarningSet warnings) {
		
		if (element.equals("motor")) {
			motorHandler = new MotorHandler(context);
			return motorHandler;
		}
		
		if (element.equals("ignitionconfiguration")) {
			ignitionConfigHandler = new IgnitionConfigurationHandler(context);
			return ignitionConfigHandler;
		}
		
		if (element.equals("ignitionevent") ||
				element.equals("ignitiondelay") ||
				element.equals("overhang")) {
			return PlainTextHandler.INSTANCE;
		}
		
		warnings.add(Warning.fromString("Unknown element '" + element + "' encountered, ignoring."));
		return null;
	}
	
	
	
	@Override
	public void closeElement(String element, HashMap<String, String> attributes,
			String content, WarningSet warnings) throws SAXException {

		// DEBUG ONLY
		// System.err.println("closing MotorMount element: "+ element);
		
		if (element.equals("motor")) {
			// yes, this is confirmed to be the FLIGHT config id, instead of the motor inastance id.
			FlightConfigurationID fcid = new FlightConfigurationID(attributes.get("configid"));
			if (!fcid.isValid()) {
				warnings.add(Warning.fromString("Illegal motor specification, ignoring."));
				return;
			}
			
			Motor motor = motorHandler.getMotor(warnings);
			MotorInstance motorInstance = motor.getNewInstance();
			motorInstance.setEjectionDelay(motorHandler.getDelay(warnings));
			mount.setMotorInstance(fcid, motorInstance);
			
			Rocket rkt = ((RocketComponent)mount).getRocket();
			rkt.createFlightConfiguration(fcid);
			
//			// vvvvvvv DEBUG vvvvvvv
//			if( mount instanceof BodyTube ){
//				System.err.println("  processing <"+element+"> element with mount: "+((RocketComponent)mount).getName()+" with content: "+content);
//				MotorInstance justSet = mount.getMotorInstance(fcid);
//				String shortKey = fcid.key.substring(0,8);
//				String motorKey = justSet.getMotorID().toString().substring(0,8);
//				String contains;
//				if( justSet.isEmpty()){
//					contains = "empty";
//				}else{
//					contains = justSet.getMotor().getDesignation();
//				}
//				System.err.println("        set( key:"+ shortKey + " to Motor: "+motorKey+ " containing: "+contains);
//				
//				// exhaustive part... 
//				
//				((BodyTube)mount).printMotorDebug( fcid );
//				
//				rkt.getConfigurationSet().printDebug();
//			}
//			// ^^^^^^^ DEBUG ^^^^^^^^
			
			return;
		}
		
		if (element.equals("ignitionconfiguration")) {
			FlightConfigurationID fcid = new FlightConfigurationID(attributes.get("configid"));
			if ( ! fcid.isValid()){
				warnings.add(Warning.fromString("Illegal motor specification, ignoring."));
				return;
			}
			
			MotorInstance inst = mount.getDefaultMotorInstance();
			// ignitionConfigHandler.getConfiguration(null); // all the parsing / loading into the confighandler should already be done...
			inst.setIgnitionDelay(ignitionConfigHandler.ignitionDelay);
			inst.setIgnitionEvent(ignitionConfigHandler.ignitionEvent);
			
			return;
		}
		
		if (element.equals("ignitionevent")) {
			IgnitionEvent event = null;
			for (IgnitionEvent ie : IgnitionEvent.values()) {
				if (ie.equals(content)) {
					event = ie;
					break;
				}
			}
			if (event == null) {
				warnings.add(Warning.fromString("Unknown ignition event type '" + content + "', ignoring."));
				return;
			}
			
			mount.getDefaultMotorInstance().setIgnitionEvent(event);
			
			return;
		}
		
		if (element.equals("ignitiondelay")) {
			double d;
			try {
				d = Double.parseDouble(content);
			} catch (NumberFormatException nfe) {
				warnings.add(Warning.fromString("Illegal ignition delay specified, ignoring."));
				return;
			}
			
			mount.getDefaultMotorInstance().setIgnitionDelay(d);
			return;
		}
		
		if (element.equals("overhang")) {
			double d;
			try {
				d = Double.parseDouble(content);
			} catch (NumberFormatException nfe) {
				warnings.add(Warning.fromString("Illegal overhang specified, ignoring."));
				return;
			}
			mount.setMotorOverhang(d);
			return;
		}
		
		super.closeElement(element, attributes, content, warnings);
	}
}