Bastille is designed to (very inefficiently) resolve the entire CWRU IP block.
Even through multithreaded nameserver queries, this process will take several hours due to rate limiting.
By resolving every IP, we are able to find which ones are still logged in the DNS, and therefore
have MAC addresses still in the CWRU whitelist.
To obtain the MAC addresses, we simply parse the default hostname into its address.
Default hostnames are given in the form: tmp*MAC*, where *MAC* is a non-delimited hexadecimal
string of length 12.
Bastille parses these hostnames into their addresses, allowing you to spoof your own address onto the network, by squatting
on a MAC address that's on the whitelist.


How do I protect myself?

change your hostname on the CWRU ITS self-service tools, seen here:
https://its-serv2.case.edu/NetworkTools/IPDB/hostnameRequestFrame.html


This is a proof-of-concept, and still provides a traceable connection through identification of the physical faceplate interface.
