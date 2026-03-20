import os
import re

def replace_in_file(filepath):
    with open(filepath, "r") as f:
        content = f.read()
    
    new_content = content.replace("import com.example.demo.entity.User;", "import com.example.demo.dos.UserDO;")
    new_content = re.sub(r"\bUser\b", "UserDO", new_content)
    new_content = new_content.replace("UserDODO", "UserDO")
    new_content = new_content.replace("UserDONotFoundException", "UserNotFoundException")
    new_content = new_content.replace("UserDOAlreadyExistsException", "UserAlreadyExistsException")
    new_content = new_content.replace("UserDOController", "UserController")
    new_content = new_content.replace("UserDOService", "UserService")
    new_content = new_content.replace("UserDOMapper", "UserMapper")
    new_content = new_content.replace("UserDODao", "UserDao")
    new_content = new_content.replace("UserDOAuthBiz", "UserAuthBiz")
    new_content = new_content.replace("UserDOCreateContext", "UserCreateContext")
    new_content = new_content.replace("UserDOPersistenceHandler", "UserPersistenceHandler")
    new_content = new_content.replace("UserDOCreateHandler", "UserCreateHandler")
    new_content = new_content.replace("UserDOType", "UserType")
    new_content = new_content.replace("UserDOReqDTO", "UserReqDTO")
    new_content = new_content.replace("UserDORspDTO", "UserRspDTO")
    new_content = new_content.replace("UserDOAuthConstant", "UserAuthConstant")
    
    if new_content != content:
        with open(filepath, "w") as f:
            f.write(new_content)

for root, dirs, files in os.walk("src/main/java/com/example/demo"):
    for file in files:
        if file.endswith(".java") and file != "UserDO.java":
            replace_in_file(os.path.join(root, file))
