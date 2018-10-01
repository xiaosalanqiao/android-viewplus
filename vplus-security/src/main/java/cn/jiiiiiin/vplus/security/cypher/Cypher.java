package cn.jiiiiiin.vplus.security.cypher;


import cn.jiiiiiin.vplus.security.exception.SecurityCypherException;

public interface Cypher {

	int EncryptType_Plain = 0;
	int EncryptType_WithName = 1;
	String LevelWeak = "W";
	String LevelStrong = "S";
	String LevelMiddle = "M";

	String encrypt(String plainorname, String modulus, String timestamp, String encoding, int flag) throws SecurityCypherException;

	String encryptWithoutRemove(String var1, String var2, String var3, String var4, int var5) throws SecurityCypherException;

	String csiiEncryptPlainText(String plaintext, String modulus, String timestamp, String encoding, int flag) throws SecurityCypherException;

	void putChar(String name, String str);

	String checkLevel(String name);

	void deleteLastPwdChar(String name);

	int getPasswordLength(String var1);

	void clearChar(String var1);

	String encryptCommon(String var1, String var2) throws Exception;
}
