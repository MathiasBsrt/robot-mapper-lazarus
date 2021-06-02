package view.components;

/**
 * 
 * @author TRAFNY Theo - UPSSITECH
 *
 */
class RobotMapThread extends Thread {
	
	/**
	 * Defines max FPS (max refresh rate) of the RobotMap
	 */
	public static final int FPS = 60;
	
	/**
	 * The RobotMap to refresh
	 */
	private RobotMap robotMap;
	
	/**
	 * Last counted FPS
	 */
	private int fps = 0;
	
	/**
	 * Is the thread running or not. Used to stop it gently.
	 */
	private boolean running = false;
	
	public RobotMapThread(RobotMap robotMap) {
		this.robotMap = robotMap;
	}
	
	@Override
	public void run() {

		/*
		 * This method is really easy to understand and is almost
		 * a game loop.
		 * 
		 * (Except that in a game loop you can found another
		 * handler called UPS that defines the maximum times that the game has to be updated per second.
		 * So the the FPS will be the maximum times that the game has to be rendered per second.)
		 * 
		 * Here we only took the rendering aspect of a game loop and we plug the
		 * update process in the render process.
		 * 
		 * So how it works:
		 * 		The loop is working with time differences.
		 * 		We compute when the game loop has to render the current scene: 1 / FPS.
		 * 		We count every time we can and every time we reach that period, we render the component.
		 * 
		 * 		But to prevent time overtaking (if the rendering process of a frame used more than one period,
		 * 			or if the computer is running slowly), we have to render every frames !
		 * 		This a the reason of using a "deltaF".
		 * 		If this variable is greater than 1, we can render (equivalent as removing 1 to "deltaF"),
		 * 			and re-render the scene straight on the next frame.
		 * 		This method prevent frames to be skipped of our animation.
		 */
		running = true;
		long initialTime = System.nanoTime();
		final double timeF = 1000000000 / FPS;
		double deltaF = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();

		while (running) {

			long currentTime = System.nanoTime();
			deltaF += (currentTime - initialTime) / timeF;
			initialTime = currentTime;

			if (deltaF >= 1) {
				// Render
				robotMap.repaint();
				frames++;
				deltaF--;
			}

			if (System.currentTimeMillis() - timer > 1000) {

				fps = frames;
				frames = 0;
				timer += 1000;
			}
		}
		
	}
	

	/**
	 * Stop the game loop
	 */
	public void dispose() {
		running = false;
	}
	
	/**
	 * Get the total frame per seconds of the passed second.
	 * @return fps frame per seconds
	 */
	public int getFPS() {
		return fps;
	}
	
}

