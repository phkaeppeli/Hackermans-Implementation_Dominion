package shared.resourceClasses;

/**
 * Message types used by the GameMessage class
 * 
 * @author Philip Käppeli
 *
 */
public enum MessageType {
	// Connection messages
	UPDATECONNECTION, HIGHSCORECONNECTION, LOBBYCONNECTION, GAMECONNECTION, HANDSHAKE,

	// Update messages
	CHECKVERSION, OLDVERSION, UPDATEFILES, VERSIONOK,

	// Highscore messages
	GETHIGHSCORES, HIGHSCOREENTRY, ADDHIGHSCORE,

	// Lobby messages
	UPDATELOBBY, SETLOBBYLEADER, LOBBYFULL, LOBBYREADY, STARTGAME, GAMERUNNING,

	// Game messages client
	NEXTPHASE, BUYCARD, PLAYCARD,

	// Game messages server
	PLAYERINDEX, PLAYERLIST, PLAYERLEFT, INITBFSTACKS, UPDATEBFSTACKS, UPDATEPHASE, UPDATEPLAYED, UPDATEHAND, UPDATEDRAW, UPDATEDISCARD, OTHERPLAYED, OTHERDISCARD, DENYBUY, DENYPLAY, ENDGAME, CANCELGAME,

	// General messages
	ENDOFCONNECTION,

	// Chat messages
	CHATMESSAGE
}
