What is Bastille?

Bastille is designed to (very inefficiently) resolve the entire CWRU IP block.
By resolving every IP, we are able to find which ones are still logged in the DNS, and therefore
have MAC addresses still in the CWRU whitelist.
To obtain the MAC addresses, we simply parse the default hostname into its address.
Default hostnames are given in the form: tmp*MAC*, where *MAC* is a non-delimited hexadecimal
string of length 12.
By parsing this into its address, we are able to spoof our own address onto the network, by squatting
on a MAC address that's on the whitelist.


How do I protect myself?

change your hostname on the CWRU ITS self-service tools, seen here:
https://its-serv2.case.edu/NetworkTools/IPDB/hostnameRequestFrame.html


Please note that this is a proof-of-concept, and still provides a traceable connection through faceplate tracing.