package fr.univavignon.courbes.physics.groupe04;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import fr.univavignon.courbes.common.Board;
import fr.univavignon.courbes.common.Direction;
import fr.univavignon.courbes.common.Item;
import fr.univavignon.courbes.common.Position;
import fr.univavignon.courbes.common.Snake;
import fr.univavignon.courbes.physics.PhysicsEngine;



/**
 * @author Castillo Quentin
 * @author Latif Alexandre
 */
public class MyPhysicsEngine implements PhysicsEngine{
	/**
	 * snakeTable[][] : tableau à 2D contenant :
	 * 1er ligne   : playerID
	 * 2ème ligne  : cos respectifs
	 * 3ème ligne  : sin respectifs 
	 */
	public double[][] snakeTable;
	/**
	 * ourBoard : Board créé permettant de placer les snakes/items
	 */
	public Board ourBoard;
	/**
	 * itemRate : taux d'apparition d'un item, permettra de faire spawn un objet
	 */
	public double itemRate;   
	
	
	public MyPhysicsEngine(int width,int height,int[] profileIds)
	{
		ourBoard = init(width,height,profileIds);
		itemRate = 1;
	}
	
	/**				VALIDÉ
	 * @param width Largeur de l'aire de jeu, exprimée en pixel.
	 * @param height Hauteur de l'aire de jeu, exprimée en pixel.
	 * @param profileIds Tableau contenant les numéros de profils des joueurs impliqués
	 * @return Un objet représentant l'aire de jeu de la manche.
	 */
	
	@Override

	public Board init(int width, int height, int[] profileIds) {
		
	Position spawn;
	int playerNbr = profileIds.length;
	snakeTable = new double[playerNbr][2]; // cos et sin 
	
	/* initialisation Board */
	ourBoard = new Board ();
	ourBoard.width = width;
	ourBoard.height = height;
	ourBoard.snakes = new Snake[playerNbr];
	ourBoard.snakesMap = new HashMap<Position, Integer>();
	ourBoard.itemsMap = new HashMap<Position, Item>();

	for (int i = 0; i < playerNbr ; i++) // CREATION + POSITIONNEMENT SNAKES
	{
		spawn = snakeSpawnPos(width, height);
		ourBoard.snakes[i] = new Snake();
		initSnake(ourBoard.snakes[i], profileIds[i] , spawn);  // CONSTRUCTEUR
		System.out.println("Snake " + Integer.toString(i) + " spawn a la position " + Integer.toString(spawn.x) + " "+Integer.toString(spawn.y));
	}
	return ourBoard;  
	}

	
	
	/**				VALIDÉ
	 * Constructeur de Snake
	 * 
	 * @param snake Snake qui va être construit
	 * @param id Id du player dans la partie
	 * @param spawnPosition Position aléatoire récupérée grâce à snakeSpawnPos
	 */
	public void initSnake(Snake snake, int id, Position spawnPosition) {
		snake.currentItems  = new HashMap<Item, Long>() ;
		snake.playerId 	    = id;
		snake.currentX      = spawnPosition.x;
		snake.currentY      = spawnPosition.y;
		snake.currentAngle  = (int)(Math.random() * 359); //Génération aléatoire d'un angle entre 0 et 359°
		snake.headRadius 	= 3;  					// 3px ?
		snake.movingSpeed   = 1;					// 1px / ms ?
		snake.turningSpeed  = 0.005; 				// ?
		snake.state 		= true;
		snake.collision 	= true;
		snake.inversion     = false;
		snake.fly   		= false;
		snake.holeRate 	    = 0.05;					// 5% ??	
		System.out.println("Angle en degré : " + Double.toString(snake.currentAngle));	
	}
	
	
	
	/**				VALIDÉ
	 * @param width Longueur du board
	 * @param height Largeur du board
	 * @return Renvoi une position (aléatoire) 
	 */
	public Position snakeSpawnPos(int width, int height){
		
		Random r = new Random();
		// Création position avec deux paramétres aléatoires et avec une marge de 20px pour éviter de spawn sur les bords
		Position pos = new Position((r.nextInt((width-20)-20)+ 20), (r.nextInt((height-20)-20)+ 20));
		return pos;
	}
	
	
	
	
	/**				PAS TERMINÉ
	 * @param elapsedTime
	 * 		Temps écoulé depuis la dernière mise à jour, exprimé en ms.
	 * @param commands
	 * 		Map associant un joueur ID à la dernière commande générée par le joueur correspondant.
	 */
	@Override
	public void update(long elapsedTime, Map<Integer, Direction> commands) {

		// update directions des snakes par rapport au temps
		updateSnakesDirections(elapsedTime, commands);
		// update coordonnées des snakes par rapport au temps
		updateSnakesPositions(elapsedTime);

		
		// TODO : Quoi d'autre a mettre a jour ? 
		// TODO : CREATION ITEM par rapport a un itemRate (déja créé dans Init)
		// TODO : UPDATE ITEM pour savoir QUAND rajouter un item
		// TODO : remplir hashmap snakes avec la TAILLE du snake
		// TODO : remplir hashmap item avec la taille des items ( 15 px ?)
	}

	
		
	/**					A AMELIORÉ (CORRECTION RECENTE PAR RAPPORT AU HASHMAP)
	 * @param time Temps écoulé
	 */
	public void updateSnakesPositions(long time){
		long alterableTime;        // temps qui passe
		double pixel;              // permet de remplir la Map du board pixel à pixel
		boolean isMoving = false;  // permet le test de collisions
		Position pos = new Position(0,0);
		
		for (int i = 0; i < ourBoard.snakes.length ; i++){
			alterableTime = time;
			pixel = 0;
						
			while(ourBoard.snakes[i].state && alterableTime > 0){   // SI SNAKE EST EN VIE & MOUVEMENTS PAS TERMINÉS
				while(alterableTime > 0 && pixel < 1){ 	// DIMINUE LE TEMPS TANT QU'ON A PAS FAIT UN PIXEL DE MOUVEMENT
					alterableTime--;
					pixel += ourBoard.snakes[i].movingSpeed;
				}
				// VALEURS SERONT COMPRISES ENTRE -2 ET 2
				snakeTable[ourBoard.snakes[i].profileId][0] += Math.cos(Math.toRadians(ourBoard.snakes[i].currentAngle)); 	
				snakeTable[ourBoard.snakes[i].profileId][1] += Math.sin(Math.toRadians(ourBoard.snakes[i].currentAngle));

				// TESTS ANGLES COS && SIN
				if(snakeTable[ourBoard.snakes[i].profileId][0] >= 1 && snakeTable[ourBoard.snakes[i].profileId][1] >= 1) {
					ourBoard.snakes[i].currentY++;
					ourBoard.snakes[i].currentX++;
					snakeTable[ourBoard.snakes[i].profileId][1]--;
					snakeTable[ourBoard.snakes[i].profileId][0]--;
					isMoving = true;
				}
				else if(snakeTable[ourBoard.snakes[i].profileId][1] <= -1 && snakeTable[ourBoard.snakes[i].profileId][0] >= 1) {
					ourBoard.snakes[i].currentY--;
					ourBoard.snakes[i].currentX++;
					snakeTable[ourBoard.snakes[i].profileId][1]++;
					snakeTable[ourBoard.snakes[i].profileId][0]--;
					isMoving = true;
				}
				else if(snakeTable[ourBoard.snakes[i].profileId][1] <= -1 && snakeTable[ourBoard.snakes[i].profileId][0] <= -1) {
					ourBoard.snakes[i].currentY--;
					ourBoard.snakes[i].currentX--;
					snakeTable[ourBoard.snakes[i].profileId][1]++;
					snakeTable[ourBoard.snakes[i].profileId][0]++;
					isMoving = true;
				}
				else if(snakeTable[ourBoard.snakes[i].profileId][1] >= 1 && snakeTable[ourBoard.snakes[i].profileId][0] <= -1) {
					ourBoard.snakes[i].currentY++;
					ourBoard.snakes[i].currentX--;
					snakeTable[ourBoard.snakes[i].profileId][1]--;
					snakeTable[ourBoard.snakes[i].profileId][0]++;
					isMoving = true;
				}
				// ON A DONC COS OU SINUS = 0, ETUDE DES DERNIERS CAS
				else if(snakeTable[ourBoard.snakes[i].profileId][1] >= 1) {
					ourBoard.snakes[i].currentY++;
					snakeTable[ourBoard.snakes[i].profileId][1]--;
					isMoving = true;
				}
				else if(snakeTable[ourBoard.snakes[i].profileId][1] <= -1) {
					ourBoard.snakes[i].currentY--;
					snakeTable[ourBoard.snakes[i].profileId][1]++;
					isMoving = true;
				}
				else if(snakeTable[ourBoard.snakes[i].profileId][0] >= 1) {
					ourBoard.snakes[i].currentX++;
					snakeTable[ourBoard.snakes[i].profileId][0]--;
					isMoving = true;
				}
				else if(snakeTable[ourBoard.snakes[i].profileId][0] <= -1) {
					ourBoard.snakes[i].currentX--;				
					snakeTable[ourBoard.snakes[i].profileId][0]++;
					isMoving = true;
				}
				else
				{
					ourBoard.snakes[i].currentY++;
					ourBoard.snakes[i].currentX--;
					snakeTable[ourBoard.snakes[i].profileId][1]--;
					snakeTable[ourBoard.snakes[i].profileId][0]++;
					isMoving = true;
				}

				pixel = 0;  
				System.out.println("New Position snake "+ Integer.toString(ourBoard.snakes[i].playerId)+ " x:" + Integer.toString(ourBoard.snakes[i].currentX) + " y:" + Integer.toString(ourBoard.snakes[i].currentY));

				if(isMoving) {    // tests de collision
					outOfBounds(ourBoard.snakes[i]);
					snakeVsSnake(ourBoard.snakes[i]);
					snakeVsItem(ourBoard.snakes[i],pos);
				}
				if(isMoving) {	// remplir le hashMap des snakes par rapport à la taille
					sizeSnakePixels(ourBoard.snakes[i]);
				}
			}
		}
	}
	
	
	
	
	
	/**				Faut Tester-> http://www.developpez.net/forums/d209/general-developpement/algorithme-mathematiques/general-algorithmique/savoir-1-point-l-interieur-d-cercle/
	 * 				racine_carre((x_point - x_centre)² + (y_centre - y_point)) < rayon ??
	 * @param snake Snake concerné
	 */
	public void sizeSnakePixels(Snake snake) { // Calculer les pixels qui doivent etre mit dans le hashmap a partir du size
		Position pos = new Position(0,0);  // contiendra les multiplise positions des pixels
		
		// faire un carré, puis récupérer les valeurs qui se trouve dans un cercle de centre snake et de diametre headRadius
		for(int i = snake.currentX - (int)snake.headRadius; i < snake.currentX + (int)snake.headRadius ; i++) {
			for(int j = snake.currentY - (int)snake.headRadius; j < snake.currentY + (int)snake.headRadius; j++) {
				if(Math.sqrt(Math.pow(i - snake.currentX, 2) + Math.pow(j - snake.currentY, 2)) < (int)snake.headRadius) {
					pos.x = i;
					pos.y = j;
					ourBoard.snakesMap.put(pos, snake.playerId);
					System.out.println("Point x:" + i + " y:" + j + " ajouté");
				}
			}
		}

	}


	/**				VALIDÉ
	 * @param snake Snake testé
	 */
	public void outOfBounds(Snake snake) {
		// Si on sort du cadre
		if(snake.currentX < 0 || snake.currentX > ourBoard.width || snake.currentY < 0|| snake.currentY > ourBoard.height) {
			if (!snake.fly) { // S'il ne peut pas traverser les murs 
				snake.state = false; 
				System.out.println(snake.playerId + " dead because of bounds!");
			} 
			else { // Envoyer le snake a l'opposé
				if(snake.currentX < 0)
					snake.currentX = ourBoard.width;
				else if(snake.currentX > ourBoard.width)
					snake.currentX = 0;
				if(snake.currentY < 0)
					snake.currentY = ourBoard.height;
				else if(snake.currentY > ourBoard.height)
					snake.currentY = 0;
			}	
		}
	}
	
	
	
	
	/**				VALIDÉ
	 * @param snake Snake testé
	 */
	public void snakeVsSnake(Snake snake) {

		Position pos = new Position(snake.currentX,snake.currentY);
		
		try
		{
			Integer idPixel = ourBoard.snakesMap.get(pos);
			
			if(idPixel == null)
			{
				ourBoard.snakesMap.put(pos , snake.playerId);  
			}
			
			else
			{
			        if (snake.collision)
			        {
				        snake.state = false;
				        System.out.println(snake.playerId + " is DEAD\nX="+pos.x+"   Y="+pos.y);
				        System.out.println("Snake n°"+snake.playerId+ " vient de dire bonjour au snake n°"+ourBoard.snakesMap.get(pos));
			        }
			}
		}catch(NullPointerException e)
		{
			System.out.println("Position non possédée, pas de collision");
		}
		
	}
	
	

	
	/**			VALIDÉ
	 * @param snake Snake testé
	 * @param pos Position du snake et de l'objet supposé
	 */
	public void snakeVsItem(Snake snake, Position pos) {
		Item newItem = ourBoard.itemsMap.get(pos);
		if( newItem != null ) {
			if(snake.state) { // if alive
				addSnakeItem(snake.playerId, newItem);
				ourBoard.itemsMap.remove(pos); // Suppression de l'item sur la map
			}
		}	
	}
	
	
	
	/**				VALIDÉ
	 * @param time Temps écoulé
	 * @param commands Commande en cours pour chaque player (LEFT,RIGHT,NONE)
	 */
	public void updateSnakesDirections(long time, Map<Integer, Direction> commands)
	{
		Direction direction; // permet de récuper la direction de chaque snakes
		for(int i = 0 ; i < ourBoard.snakes.length ; i++)
		{
			direction = commands.get(ourBoard.snakes[i].playerId); // LEFT/RIGHT/NONE
			if(direction != null)
			{
				switch (direction)
				{
					case LEFT: // Si on va a gauche, il faut augmenter l'angle
						if(ourBoard.snakes[i].inversion) // test en cas d'inversion, si tout va bien, incrémentation de l'angle en fonction de la vitesse de rotation
							ourBoard.snakes[i].currentAngle -= time*Math.toDegrees(ourBoard.snakes[i].turningSpeed);
						else
							ourBoard.snakes[i].currentAngle += time*Math.toDegrees(ourBoard.snakes[i].turningSpeed);
						break;
					case RIGHT: // Si on va à droite, il faut diminuer l'angle
						if(ourBoard.snakes[i].inversion)
							ourBoard.snakes[i].currentAngle += time*Math.toDegrees(ourBoard.snakes[i].turningSpeed);
						else
							ourBoard.snakes[i].currentAngle -= time*Math.toDegrees(ourBoard.snakes[i].turningSpeed);
						break;
					case NONE: // rien ne change
						break;
					default:
						System.out.println("ERROR ???");
						break;
					}
			}
		}
	}
	
	
	
	/**						VALIDÉ
	 * Mise à jour forcé du board courant
	 * 
	 * @param board Board qui va écraser le board courant
	 */
	@Override
	public void forceUpdate(Board board) {
		this.ourBoard = board;
	}
	
	
	
	/**						VALIDÉ
	 * Nouvel item, associé à un ou plusieurs snakes
	 * 
	 * @param id Id player
	 * @param item Item concerné
	 */
	public void addSnakeItem(int id, Item item) {
		switch(item){
			case USER_SPEED:
				ourBoard.snakes[id].currentItems.put(item, (long)item.duration);
				ourBoard.snakes[id].movingSpeed *= 2;
				break;
			case USER_SLOW:
				ourBoard.snakes[id].currentItems.put(item, (long)item.duration);
				ourBoard.snakes[id].movingSpeed /= 1.5;
				break;
			case USER_BIG_HOLE:
				ourBoard.snakes[id].currentItems.put(item, (long)item.duration);
				ourBoard.snakes[id].holeRate /= 1.5;
				break;
			case OTHERS_SPEED:
				for(Snake snake : ourBoard.snakes) {
					if (snake.playerId != id) {
						snake.currentItems.put(item, (long)item.duration);
						snake.movingSpeed *= 2;
					}
				}
				break;
			case OTHERS_THICK:
				for(Snake snake : ourBoard.snakes) {
					if (snake.playerId != id) {
						snake.currentItems.put(item, (long)item.duration);
						snake.headRadius *= 1.5;
					}
				}
				break;
			case OTHERS_SLOW:
				for(Snake snake : ourBoard.snakes) {
					if (snake.playerId != id) {
						snake.currentItems.put(item, (long)item.duration);
						snake.movingSpeed /= 1.5;
					}
				}
				break;
			case OTHERS_REVERSE:
				for(Snake snake : ourBoard.snakes) {
					if (snake.playerId != id) {
						snake.currentItems.put(item, (long)item.duration);
						snake.inversion = true;
					}
				}
				break;
			case COLLECTIVE_THREE_CIRCLES:
				for(int i = 0 ; i<3;i++)
				{
					// TODO  : GENEREATE RANDOM ITEM
				}
				break;
			case COLLECTIVE_TRAVERSE_WALL:
				for(Snake snake : ourBoard.snakes) {
					snake.currentItems.put(item, (long)item.duration);
					snake.fly = true;
				}
				break;
			case COLLECTIVE_ERASER:
				ourBoard.snakesMap.clear();
				break;
			default:
				System.out.println("Error ??");
				break;
			}
	}


	
	
	/**						VALIDÉ
	 * Effet Item terminé
	 * 
	 * @param id Id player
	 * @param item Item concerné
	 */
	
	public void removeSnakeItem(int id, Item item) {
		
		switch(item)
		{
			case USER_SPEED:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].movingSpeed /= 2;
				break;
			case USER_SLOW:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].movingSpeed *= 1.5;
				break;
			case USER_BIG_HOLE:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].holeRate *= 1.5;
				break;
			case OTHERS_SPEED:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].movingSpeed /= 2;
				break;
			case OTHERS_THICK:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].headRadius /= 1.5;
				break;
			case OTHERS_SLOW:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].movingSpeed *= 1.5;
				break;
			case OTHERS_REVERSE:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].inversion = false;
				break;
			case COLLECTIVE_TRAVERSE_WALL:
				ourBoard.snakes[id].currentItems.remove(item);
				ourBoard.snakes[id].fly = false;
				break;
			default:
				System.out.println("Error ???");
				break;
		}
	}

	
}

// IDEE ALEX UPDATESNAKEPOSITION

/*			// COMPARAISON entre snakeTable & ourBoard.snakes[i].current angle

if (snakeTable[ourBoard.snakes[i].profileId][0] > Math.cos(Math.toRadians(ourBoard.snakes[i].currentAngle))){
     
     ourBoard.snakes[i].currentX--; 
     
}
else if ( snakeTable[ourBoard.snakes[i].profileId][0] < Math.cos(Math.toRadians(ourBoard.snakes[i].currentAngle))) {
     ourBoard.snakes[i].currentX++;
    
}

else{
     if (snakeTable[ourBoard.snakes[i].profileId][0] > 0){
          ourBoard.snakes[i].currentX++;
     }
     
     else if (snakeTable[ourBoard.snakes[i].profileId][0] < 0){
          ourBoard.snakes[i].currentX--;
     }
}

if (snakeTable[ourBoard.snakes[i].profileId][1] > Math.sin(Math.toRadians(ourBoard.snakes[i].currentAngle))){
     
     ourBoard.snakes[i].currentY--; 
     
}
else if ( snakeTable[ourBoard.snakes[i].profileId][1] < Math.sin(Math.toRadians(ourBoard.snakes[i].currentAngle))) {
     ourBoard.snakes[i].currentY++;
    
}

else{
     if (snakeTable[ourBoard.snakes[i].profileId][1] > 0){
          ourBoard.snakes[i].currentY++;
     }
     
     else if (snakeTable[ourBoard.snakes[i].profileId][1] < 0){
          ourBoard.snakes[i].currentY--;
     }
}*/

