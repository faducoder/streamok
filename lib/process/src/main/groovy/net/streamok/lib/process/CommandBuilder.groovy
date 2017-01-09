/**
 * Licensed to the Streamok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.lib.process

import com.google.common.collect.ImmutableMap

class CommandBuilder {

    // Required members

    private final List<String> command

    // Optional members

    private File workingDirectory

    private Map<String, String> environment

    private boolean sudo = false

    private String sudoPassword

    // Constructors

    CommandBuilder(List<String> command) {
        this.command = command
    }

    CommandBuilder(String... command) {
        this.command = command.toList()
    }

    // Factory methods

    static CommandBuilder cmd(String... command) {
        if(command.length == 1 && command[0] =~ /\s+/) {
            cmd(command[0].split(/\s+/))
        } else {
            new CommandBuilder(command.toList())
        }
    }

    static CommandBuilder sudo(String... command) {
        cmd(command).sudo()
    }

    // Build methods

    Command build() {
        new Command(command, workingDirectory, environment, sudo, sudoPassword)
    }

    // Setters

    CommandBuilder workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory
        this
    }

    CommandBuilder environment(Map<String, String> environment) {
        this.environment = ImmutableMap.copyOf(environment)
        this
    }

    CommandBuilder sudo(boolean sudo) {
        this.sudo = sudo
        this
    }

    CommandBuilder sudo() {
        this.sudo(true)
        this
    }

    CommandBuilder sudoPassword(String sudoPassword) {
        this.sudoPassword = sudoPassword
        this
    }

}
