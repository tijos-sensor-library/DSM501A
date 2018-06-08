package tijos.framework.sensor.dsm501a;

import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.eventcenter.ITiEvent;
import tijos.framework.eventcenter.ITiEventListener;
import tijos.framework.eventcenter.TiEventService;
import tijos.framework.eventcenter.TiEventType;
import tijos.framework.eventcenter.TiGPIOEvent;
import tijos.framework.util.Delay;

/**
 * DSM501A
 *
 */
public class TiDSM501A implements ITiEventListener {
	/**
	 * TiGPIO object
	 */
	private TiGPIO gpioObj = null;

	private int pin;

	private long lowPulseOccupancy = 0;

	private long lowTimeStart = 0;

	private long sampleTime = 0;


	public TiDSM501A(TiGPIO gpio, int pinID, int sampleTime) {

		this.gpioObj = gpio;
		this.pin = pinID;
		this.sampleTime = sampleTime;

		this.lowTimeStart = 0;
		this.lowPulseOccupancy = 0;
	}

	/**
	 * initialize the GPIO pin
	 * 
	 * @throws IOException
	 */
	public void initialize() throws IOException {

		this.gpioObj.setWorkMode(this.pin, TiGPIO.INPUT_PULLUP);
		this.gpioObj.setEventParameters(this.pin, TiGPIO.EVT_BOTHEDGE, 1000);

		this.lowPulseOccupancy = 0;
		TiEventService.getInstance().addListener(this);
	}

	/**
	 * @return the mg/m3 concentration
	 * @throws IOException
	 */
	public double readConcentration() throws IOException {

		this.lowPulseOccupancy = 0;

		if (this.gpioObj.readPin(this.pin) == 0)
			this.lowTimeStart =  System.currentTimeMillis();

		//Delay for data log
		Delay.msDelay(this.sampleTime);

		// Integer percentage 0=>100
		double ratio = this.lowPulseOccupancy/ (this.sampleTime);

		// using spec sheet curve
		double concentration = 1.1 * Math.pow(ratio, 3) - 3.8 * Math.pow(ratio, 2) + 520 * ratio + 0.62;

		return concentration;
	}

	@Override
	public TiEventType getType() {
		return TiEventType.GPIO;
	}

	@Override
	public void onEvent(ITiEvent evt) {

		synchronized (this) {
			TiGPIOEvent eventObj = (TiGPIOEvent) evt;
			if (eventObj.getPin() == this.pin && eventObj.getEvent() == TiGPIO.EVT_FALLINGEDGE) {
				lowTimeStart = eventObj.getTime();
			}

			if (eventObj.getPin() == this.pin && eventObj.getEvent() == TiGPIO.EVT_RISINGEDGE) {

				if (this.lowTimeStart > 0) {
					long duration = eventObj.getTime() - lowTimeStart;
					
					//Convert to ms from us
					this.lowPulseOccupancy += (duration / 1000);
				}
			}

		}
	}

}
