package tijos.framework.sensor.dsm501a;

import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;


/**
 * Hello world!
 *
 */
public class TiDSM501ASample
{
    public static void main( String[] args )
    {
    	/*
		 * 定义使用的TiGPIO port
		 */
		int gpioPort0 = 0;
		
		/*
		 * 定义所使用的gpioPin
		 */
		int gpioPin0 = 0;
		
		try {
			
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
			
			TiDSM501A dsm501a = new TiDSM501A(gpio0, gpioPin0, 30000);
			
			dsm501a.initialize();
			
			double Concentration = dsm501a.readConcentration();
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
    	
    }
}
