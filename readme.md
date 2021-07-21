## OpenPGP Crypto GUI
An interactive GUI application which implements the Open PGP protocol. All of the functionalities are implemented according to the [RFC 4480](https://datatracker.ietf.org/doc/html/rfc4880) standard. The application is fully compatible with other applications based on the Open PGP protocol (e.g., [GnuPG Kleopatra](https://www.openpgp.org/software/kleopatra/)).

Supported asymmetric cryptography algorithms:
- DSA with 1024/2048-bit keys for signing and ElGamal with 1024/2048/4096-bit keys for encryption

Supported symmetric cryptography algorithms:
- Triple DES with EDE configuration and 3 keys 
- CAST5 with 128-bit key

## Libraries
- [JavaFX](https://openjfx.io/)
- [Bouncy Castle Crypto API](https://www.bouncycastle.org/java.html)

## Features
- Storing, deleting and generating new public/secret key rings
- Importing and exporting public/secret key rings in .asc formats
- Public/secret key ring table overview
- File encryption and signing (with optional radix-64 conversion and ZIP compression)
- File decryption and signature verification

## User Interface

- Main menu
<p align="center">
<img src="https://user-images.githubusercontent.com/61201104/126529897-f1bebe00-69eb-4b22-a6b3-3bd86654ea9a.png" alt="main_menu" width=60%/>
</p>

- Key management
<p align="center">
<img src="https://user-images.githubusercontent.com/61201104/126532674-819c05d3-3dfb-47d6-b3f3-f8f144cb9885.png" alt="enc" width=60%/>
</p>

- Encryption/Signing
<p align="center">
<img src="https://user-images.githubusercontent.com/61201104/126531705-090c854a-eb9f-41b7-96cb-08704d226598.png" alt="enc" width=60%/>
</p>

- Decryption/Verification
<p align="center">
<img src="https://user-images.githubusercontent.com/61201104/126532024-cd20abe5-05f8-4dc1-8890-501659ce802a.png" alt="dec" width=60%/>
</p>
