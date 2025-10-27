# Passer

**Passer** is a minimalist CLI password manager focused on security, transparency, and full control over your data.  
It doesn’t require cloud services, has no graphical interface, and is entirely operated from the terminal.

## What does "cryptographically secure" mean?

1. The master key is not used directly as a password but serves as input material for one of the available KDFs with a large number of iterations by default.  
   This significantly reduces the risk of brute-force attacks.
2. Modern and time-tested cryptographic algorithms such as `AES-GCM` and `PBKDF2` are used to encrypt passwords and related information.
3. Extracting passwords from a memory dump is extremely difficult since all critical data is additionally encrypted in memory using a combined approach (`AES-CBC`, `SHA-2`, `XOR`, and a semi-dynamic salt bound to class names).

## Why is it simple?

1. The command set is minimal and easy to remember.  
2. Each command is straightforward and user-friendly.  
3. You can’t accidentally lose data, for example, by closing a file without saving - the program will always warn about potential data loss.  
4. All passwords are stored only in local encrypted files. Operation is fully transparent and predictable.

## Differences from other password managers

| Manager | Local Encryption | CLI | Code Transparency | Dependencies |
|----------|------------------|-----|------------------|--------------|
| **Passer** | ✅ AES-GCM, PBKDF2, AES-CBC, SHA-2 | ✅ Native | ✅ Fully open source | 🔹 Only **JRE** (no external libraries other than the lightweight JOpt Simple) |
| KeePass / KeePassXC | ✅ AES, ChaCha20 | ⚙️ Via plugins / KeePassXC-CLI | ✅ Open-source (C# / C++) | ⚙️ .NET / Qt, plugins, GUI shell |
| Bitwarden | ✅ AES-CBC, PBKDF2, Argon2 | ⚙️ CLI via REST API | ⚙️ Partially open (Bitwarden Cloud server is closed) | ⚙️ Node.js, npm packages, API, server component |
| 1Password | ✅ Proprietary format (published but closed) | ⚙️ CLI client | ❌ Closed source | ⚙️ Proprietary API, SDK, encryption beyond user control |

### Passer’s strengths compared to others

#### Local and predictable encryption
- Passer doesn’t use databases - everything is stored in simple encrypted binary files.  
- No network communication, background sync, or external APIs.  
- Minimizes the risk of data compromise via the cloud or dependencies.

#### In-memory encryption
- Unlike most managers (KeePass, Bitwarden), where passwords can temporarily exist unencrypted in the heap,  
  **Passer keeps sensitive data encrypted in memory and decrypts only when accessed.**
- After use, all sensitive data is immediately wiped (by zeroing arrays and buffers).  
- The in-memory encryption algorithm combines `AES-CBC + SHA-2 + XOR + salt derived from class structure`, making extraction from memory dumps extremely difficult.

#### Minimal dependencies and code footprint
- Passer doesn’t rely on cryptographic frameworks (BouncyCastle, Sodium, etc.) - only standard **Java** libraries.  
- This increases code trustworthiness and eliminates external vulnerability risks.  
- The code is easily auditable and runs on any JRE without third-party libraries.

#### Simple yet strong key model
- The master password is never used directly - it’s processed through a KDF (PBKDF2) with a high iteration count (2 million).  
  This prevents dictionary and brute-force attacks.  
- All derived keys are isolated and never saved to disk.  
- File data is additionally authenticated using AES-GCM, which protects against tampering as well as reading.

#### CLI without compromises
- Passer’s interface is minimalistic yet powerful - everything is done via text commands.  
- Errors and state mismatches are handled explicitly, with warnings and safe fallbacks.  
- The CLI behaves identically on all platforms where **Java** is available.

### When Passer is a better choice

| Scenario | Why Passer is better |
|-----------|----------------------|
| You don’t want to depend on the cloud | Fully offline |
| You want transparent and auditable code | Open source, minimal dependencies |
| You care about in-memory protection | Data in RAM is encrypted |
| You work on servers or in terminals | CLI-only, no GUI dependencies |
| You want full control over files | Simple file-based model, no databases |

## Security Details

- Passwords are never stored in plaintext.  
- The master password, key, and derived values are never written to disk.  
- All sensitive data (master key and user passwords) remain encrypted in memory and are temporarily decrypted only when used, then securely wiped.  
- Losing the master password makes recovery impossible.

## Installation

Download the latest release from the **Releases** section.

Requires **Java Runtime Environment 17** or later.

You can run the program using the command `java -jar Passes.jar`

## Example usage

```cmd
>> usage make

Argument      Description
--------      -----------
[filepath]   path to file

Option     Description
------     -----------
-h, --hex  Specify password type is HEX

>> make test.passer
enter password:
file successfully created

>> usage add
Option (* = required)            Description
---------------------            -----------
* -c, --cap, --caption <String>  Caption of password
-g, --gen, --generate            Specify that the password should be generated
                                   automatically
-l, --log, --login <String>      Login of password
-s, --service, --srv <String>    Service of password
-t, --type <[TEXT,BINARY]>       Type of password

>> add -c CoolPassword -l mail@example.com -s example.shop.com -g
new password with caption "CoolPassword" successfully added and its id is "0"

>> list
ID     Caption                                        Login                         Service
0      CoolPassword                                   mail@example.com              example.shop.com

>> info 0
caption: CoolPassword
login: mail@example.com
service: example.shop.com
password type: text
is password autogenerated: true
creation time: 2025-10-27 21:44:45 (UTC+9:00)
modification time: 2025-10-27 21:44:45 (UTC+9:00)

>> copy 0
password has been copied to the clipboard and will be erased from there in 1 minute

>> save
file successfully saved

>> close
file closed

>> exit
```

## Building

This section will be expanded later.

You can build the program in **IntelliJ IDEA** by cloning the repository, creating a new artifact, and setting the main class to `com.github.onran0.passer.cli.PasserCLI`.
