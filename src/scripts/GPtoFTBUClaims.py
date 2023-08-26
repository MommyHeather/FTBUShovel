import re
import math
import json
from nbt import nbt #hard depends sorry
import requests
import yaml #hard depends sorry

#non_decimal = re.compile(r"([+-]?(?=\.\d|\d)(?:\d+)?(?:\.?\d*))(?:[Ee]([+-]?\d+))?", re.IGNORECASE) #re.compile(r'[^\d.]+')
#fuck regex

dim_whitelist = ["-","0","1","2","3","4","5","6","7","8","9"]

#_nextClaimID should be skipped. All others should be parsed.
#FTBU's claim data needs to be all in one dictionary. yay.

outClaims = {}

#We also need to buid an LMPlayers.txt and LMPlayers.dat. Fun.
#Yes, this means we need to work with NBT.

outNBT = nbt.NBTFile()
outNBT.name = "LMPlayers.dat"

nbtPlayers = nbt.TAG_Compound(None, "Players")

checkedPlayers = []

outTXT = open("LMPlayers.txt", "w")

#GP stores stuff in a folder named ClaimData.
#Therefore, that's where we'll look.

#Essentials stores data in a folder called userdata. 
#Stick it next to the ClaimData folder. If you're not using essentials for homes, just leave out the folder.
import os
os.chdir("ClaimData")

for name in os.listdir("./"):
    if name == "_nextClaimID":
        continue


    with open(name) as f:

        try :
            #First line. Location.
            line = f.readline()
            #It's all semicolon separated.
            separated = line.split(";")
            #Grab dimension.
            if separated[0] == "World":
                dim = 0
            else:
                dim = int("".join(c for c in separated[0] if c in dim_whitelist))

            #Grab chunkX.
            startX = math.floor(int(separated[1]) / 16)
            #Y can be discarded.
            #Grab chunkZ.
            startZ = math.floor(int(separated[3]) / 16)

            #Repeat X and Z for second line. Dim and Y can be discarded.
            line = f.readline()
            separated = line.split(";")

            endX = math.floor(int(separated[1]) / 16)
            
            endZ = math.floor(int(separated[3]) / 16)

            #Owner UUID is third line.
            uuid = f.readline().replace("\n", "")
            if uuid == "" :
                raise Exception("UUID is blank!")

            #The rest can be discarded.


            
            #Write data. DIM, then UUID, then each claimed chunk is a list of two ints.
            if dim not in outClaims :
                outClaims[dim] = {}
            
            if uuid not in outClaims[dim] :
                outClaims[dim][uuid] = []
            
            for i in range(startX, endX):
                for j in range(startZ, endZ):
                    outClaims[dim][uuid].append(
                        [i, j]
                    )

            if uuid not in checkedPlayers:
                data = requests.get(f"https://sessionserver.mojang.com/session/minecraft/profile/{uuid.replace('-','')}").json()
                name = data["name"]
                print(name)
                checkedPlayers.append(uuid)
                player = nbt.TAG_Compound(None, f"{len(checkedPlayers)}")
                settings = nbt.TAG_Compound(None, "Settings")
                homes = nbt.TAG_Compound(None, "Homes")
                settings.tags.append(
                    nbt.TAG_Byte(1, "Badge")
                )
                settings.tags.append(
                    nbt.TAG_Byte(2, "Blocks")
                )
                settings.tags.append(
                    nbt.TAG_Byte(7, "Flags")
                )
                player.tags.append(
                    nbt.TAG_String(uuid, "UUID")
                )
                player.tags.append(
                    nbt.TAG_String(name, "Name")
                )

                player.tags.append(settings)

                try :
                    with open(f"../userdata/{name}.yml") as f:
                        userdata = yaml.safe_load(f)
                    if "homes" in userdata.keys():
                        homes2 = userdata["homes"]
                        for key in homes2.keys():

                            if homes2[key]["world"] == "World":
                                dim = 0
                            else:
                                dim = int("".join(c for c in homes2[key]["world"] if c in dim_whitelist)) #
                            """
                            values = nbt.TAG_List(type=nbt.TAG_Int, name=key)
                            values.tags.append(nbt.TAG_Int(int(homes2[key]["x"])))
                            values.tags.append(nbt.TAG_Int(int(homes2[key]["y"])))
                            values.tags.append(nbt.TAG_Int(int(homes2[key]["z"])))
                                
                            values.tags.append(nbt.TAG_Int(int(dim)))
                            homes.tags.append(values)"""

                            values = nbt.TAG_Int_Array(key)
                            
                            values.value =  [
                            int(homes2[key]["x"]),
                            int(homes2[key]["y"]),
                            int(homes2[key]["z"]),
                            int(dim)
                            ]

                                
                            homes.tags.append(values)

                        player.tags.append(homes)
                            

                
                except FileNotFoundError:
                    pass

                nbtPlayers.tags.append(player)
                outTXT.write(f"{len(checkedPlayers)}     {name}         {uuid}\n")

        
        except Exception as e:
            print(f"Failed to parse file {name} : {e}")

#Finally, write output.
os.chdir("../")
with open("./ClaimedChunks.json", "w") as f:
    json.dump(outClaims, f, indent=4)


outNBT.tags.append(nbtPlayers)
outNBT.tags.append(nbt.TAG_Int(len(checkedPlayers), "LastID"))
outNBT.write_file("LMPlayers.dat")

outTXT.close()